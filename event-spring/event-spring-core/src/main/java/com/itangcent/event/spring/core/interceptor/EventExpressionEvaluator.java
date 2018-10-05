package com.itangcent.event.spring.core.interceptor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventExpressionEvaluator {

    /**
     * The name of result object.
     */
    public static final String RESULT_VARIABLE = "result";

    public static final Object RESULT_UNAVAILABLE = new Object();

    private final SpelExpressionParser parser;

    protected ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final Map<String, Expression> cache = new ConcurrentHashMap<>(64);

    public EventExpressionEvaluator(SpelExpressionParser parser) {
        Assert.notNull(parser, "SpelExpressionParser must not be null");
        this.parser = parser;
    }

    public EventExpressionEvaluator() {
        this(new SpelExpressionParser());
    }

    public SpelExpressionParser getParser() {
        return this.parser;
    }

    protected void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return parameterNameDiscoverer;
    }

    protected Expression getExpression(String expression) {
        return cache.computeIfAbsent(expression, parser::parseExpression);
    }

    @Nullable
    public Object eval(String expression, EvaluationContext evalContext) {
        return getExpression(expression).getValue(evalContext);
    }

    public boolean evalBool(String expression, EvaluationContext evalContext) {
        return (Boolean.TRUE.equals(getExpression(expression).getValue(
                evalContext, Boolean.class)));
    }

    public EvaluationContext createEvaluationContext(Method method, Object[] args, Object target, Class<?> targetClass,
                                                     Method targetMethod,
                                                     @Nullable Object result, @Nullable BeanFactory beanFactory) {

        EventExpressionRootObject rootObject = new EventExpressionRootObject(method, args, target, targetClass);
        EventEvaluationContext evaluationContext = new EventEvaluationContext(
                rootObject, targetMethod, args, getParameterNameDiscoverer());
        if (result == RESULT_UNAVAILABLE) {
            evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
        } else if (result != null) {
            evaluationContext.setVariable(RESULT_VARIABLE, result);
        }
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

}
