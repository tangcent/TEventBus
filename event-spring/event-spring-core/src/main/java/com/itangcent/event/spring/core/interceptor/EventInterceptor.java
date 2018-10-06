package com.itangcent.event.spring.core.interceptor;

import com.itangcent.event.EventBus;
import com.itangcent.event.TopicEvent;
import com.itangcent.event.annotation.Stage;
import com.itangcent.event.spring.core.ArrSwap;
import com.itangcent.event.spring.core.ComponentEventBus;
import com.itangcent.event.spring.core.EventBusManager;
import com.itangcent.event.utils.Assert;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.aop.support.AopUtils.getTargetClass;

public class EventInterceptor implements MethodInterceptor, BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    private boolean initialized = false;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private EventBusManager eventBusManager;

    private EventExpressionEvaluator eventExpressionEvaluator = new EventExpressionEvaluator();

    private EventInfoExtractor eventInfoExtractor = new DefaultEventInfoExtractor();
    private static EventInterceptor instance = new EventInterceptor();

    public static EventInterceptor instance() {
        return instance;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

        EventMethodInvoker aopAllianceInvoker = new MethodInvocationInvoker(invocation);
        Method method = invocation.getMethod();

        try {
            return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
        } catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }

    private Object execute(EventMethodInvoker invoker, Object target, Method method, Object[] args) {

        if (this.initialized) {
            Class<?> targetClass = getTargetClass(target);
            EventInfoExtractor eventInfoExtractor = getEventInfoExtractor();
            if (eventInfoExtractor != null) {
                Collection<EventInfo> eventInfos = eventInfoExtractor.extract(method, targetClass);
                if (!CollectionUtils.isEmpty(eventInfos)) {
                    return execute(invoker, target, method, args, new EventInfoContext(eventInfos));
                }
            }
        }

        return invoker.invoke();
    }

    private class EventInfoContext {

        private final MultiValueMap<Stage, EventPublishInfo> publishInfos;

        public EventInfoContext(Collection<? extends EventInfo> eventInfos) {

            this.publishInfos = new LinkedMultiValueMap<>(eventInfos.size());
            for (EventInfo eventInfo : eventInfos) {
                if (eventInfo instanceof EventPublishInfo) {//support publishInfo only
                    this.publishInfos.add(((EventPublishInfo) eventInfo).getStage(), (EventPublishInfo) eventInfo);
                }
                //todo:support others
            }
        }

        public Collection<EventPublishInfo> getPublishInfo(Stage stage) {
            return publishInfos.get(stage);
        }
    }

    private Object execute(EventMethodInvoker invoker, Object target, Method method, Object[] args, EventInfoContext eventInfoContext) {

        Method originalMethod = BridgeMethodResolver.findBridgedMethod(method);

        EventExecuteContext eventExecuteContext = new EventExecuteContext(target, method, originalMethod,
                args, EventExpressionEvaluator.RESULT_UNAVAILABLE, null);

        try {
            Collection<EventPublishInfo> beforePublishEventInfo = eventInfoContext.getPublishInfo(Stage.BEFORE);
            executePublishEvent(beforePublishEventInfo, eventExecuteContext);

            eventExecuteContext.setRtn(invoker.invoke());

            Collection<EventPublishInfo> afterPublishEventInfo = eventInfoContext.getPublishInfo(Stage.AFTER);
            executePublishEvent(afterPublishEventInfo, eventExecuteContext);

            return eventExecuteContext.getRtn();
        } catch (EventMethodInvoker.ThrowableWrapper wrapper) {
            eventExecuteContext.setError(wrapper.getOriginal());
            Collection<EventPublishInfo> errorPublishEventInfo = eventInfoContext.getPublishInfo(Stage.ERROR);
            executePublishEvent(errorPublishEventInfo, eventExecuteContext);
            throw wrapper;
        } finally {
            Collection<EventPublishInfo> finallyPublishEventInfo = eventInfoContext.getPublishInfo(Stage.FINALLY);
            executePublishEvent(finallyPublishEventInfo, eventExecuteContext);
        }
    }

    private class EventExecuteContext {
        private final Class<?> targetClass;
        private Object target;
        private Method method;
        private Method originalMethod;
        private Object[] args;
        private Object rtn;
        private Throwable error;

        public EventExecuteContext(Object target, Method method, Method originalMethod, Object[] args, Object rtn, Throwable error) {
            this.target = target;
            this.method = method;
            this.originalMethod = originalMethod;
            this.args = args;
            this.rtn = rtn;
            this.error = error;
            this.targetClass = AopProxyUtils.ultimateTargetClass(target);
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Method getOriginalMethod() {
            return originalMethod;
        }

        public void setOriginalMethod(Method originalMethod) {
            this.originalMethod = originalMethod;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }

        public Object getRtn() {
            return rtn;
        }

        public void setRtn(Object rtn) {
            this.rtn = rtn;
        }

        public Throwable getError() {
            return error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }
    }

    private void executePublishEvent(Collection<EventPublishInfo> publishEventInfos,
                                     EventExecuteContext eventExecuteContext) {
        if (CollectionUtils.isEmpty(publishEventInfos)) {
            return;
        }

        EvaluationContext evaluationContext = eventExpressionEvaluator.createEvaluationContext(eventExecuteContext.getMethod(),
                eventExecuteContext.getArgs(),
                eventExecuteContext.getTarget(),
                eventExecuteContext.getTargetClass(),
                eventExecuteContext.getOriginalMethod(),
                eventExecuteContext.getRtn(),
                beanFactory);
        for (EventPublishInfo publishEventInfo : publishEventInfos) {
            //condition
            String condition = publishEventInfo.getCondition();
            if (!StringUtils.isEmpty(condition)) {
                if (!eventExpressionEvaluator.evalBool(condition, evaluationContext)) {
                    continue;
                }
            }

            //unless
            String unless = publishEventInfo.getUnless();
            if (!StringUtils.isEmpty(unless)) {
                if (!eventExpressionEvaluator.evalBool(unless, evaluationContext)) {
                    continue;
                }
            }


            String eventExp = publishEventInfo.getEvent();
            Object event = eventExpressionEvaluator.eval(eventExp, evaluationContext);

            publish(event, publishEventInfo.getTopic(), publishEventInfo.getTo());
        }
    }

    private void publish(Object event, String[] topics, String[] to) {
        EventBus eventBus = findEventBus(to);
        if (topics.length == 0) {
            eventBus.post(event);
        } else {
            for (String topic : topics) {
                eventBus.post(new TopicEvent(event, topic));
            }
        }
    }

    private Map<ArrSwap<String>, EventBus> busCache = new ConcurrentHashMap<>();

    private EventBus findEventBus(String[] busName) {
        EventBus eventBus = busCache.get(new ArrSwap<>(busName));
        if (eventBus == null) {
            EventBusManager eventBusManager = getEventBusManager();
            assert eventBusManager != null;

            if (busName.length == 0) {
                eventBus = eventBusManager.eventBuses();
            } else {
                if (busName.length == 1) {
                    eventBus = eventBusManager.getEventBus(busName[0]);
                } else {
                    EventBus[] toEventBuses = new EventBus[busName.length];
                    for (int i = 0; i < busName.length; i++) {
                        toEventBuses[i] = eventBusManager.getEventBus(busName[i]);
                    }
                    eventBus = new ComponentEventBus(toEventBuses);
                }
            }
            busCache.put(new ArrSwap<>(busName), eventBus);
        }
        return eventBus;
    }

    private EventInfoExtractor getEventInfoExtractor() {
        return eventInfoExtractor;
    }

    @Nullable
    public EventBusManager getEventBusManager() {
        if (eventBusManager == null) {
            eventBusManager = beanFactory.getBean(EventBusManager.class);
            Assert.notNull(eventBusManager, "no eventBusManager be found!");
        }
        return eventBusManager;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void afterSingletonsInstantiated() {
        this.initialized = true;
    }
}
