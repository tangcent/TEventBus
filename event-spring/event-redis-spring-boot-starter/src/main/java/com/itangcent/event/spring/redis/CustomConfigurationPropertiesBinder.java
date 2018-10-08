package com.itangcent.event.spring.redis;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class CustomConfigurationPropertiesBinder implements InitializingBean {

    private ApplicationContext applicationContext;
    private ConversionService conversionService;
    private PropertySources propertySources;
    private Binder binder;
    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;

    CustomConfigurationPropertiesBinder(ApplicationContext applicationContext,
                                        ConversionService conversionService) {
        this.applicationContext = applicationContext;
        this.conversionService = conversionService;
        this.propertySources = new CustomPropertySourcesDeducer(applicationContext).getPropertySources();
    }


    void bind(Object bean, String beanName) {
        ResolvableType type = getBeanType(bean, beanName);
        ConfigurationProperties annotation = getAnnotation(bean, beanName,
                ConfigurationProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(bean)
                .withAnnotations(annotation);
        bind(target);
    }

    void bind(Object bean, String beanName, String prefix) {
        ResolvableType type = getBeanType(bean, beanName);
        ConfigurationProperties annotation = getAnnotation(bean, beanName,
                ConfigurationProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(bean)
                .withAnnotations(annotation);
        bind(target, prefix);
    }

    void bind(Bindable<?> target, String prefix) {
        ConfigurationProperties annotation = target
                .getAnnotation(ConfigurationProperties.class);
        Assert.state(annotation != null,
                () -> "Missing @ConfigurationProperties on " + target);
        BindHandler bindHandler = getBindHandler(annotation);
        getBinder().bind(prefix, target, bindHandler);
    }

    private ResolvableType getBeanType(Object bean, String beanName) {
        Method factoryMethod = this.beanFactoryMetadata.findFactoryMethod(beanName);
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return ResolvableType.forClass(bean.getClass());
    }

    public void bind(Bindable<?> target) {
        ConfigurationProperties annotation = target
                .getAnnotation(ConfigurationProperties.class);
        Assert.state(annotation != null,
                () -> "Missing @ConfigurationProperties on " + target);
        BindHandler bindHandler = getBindHandler(annotation);
        getBinder().bind(annotation.prefix(), target, bindHandler);
    }

    private BindHandler getBindHandler(ConfigurationProperties annotation) {
        BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
        if (annotation.ignoreInvalidFields()) {
            handler = new IgnoreErrorsBindHandler(handler);
        }
        if (!annotation.ignoreUnknownFields()) {
            UnboundElementsSourceFilter filter = new UnboundElementsSourceFilter();
            handler = new NoUnboundElementsBindHandler(handler, filter);
        }
        return handler;
    }

    private <A extends Annotation> A getAnnotation(Object bean, String beanName,
                                                   Class<A> type) {
        A annotation = this.beanFactoryMetadata.findFactoryAnnotation(beanName, type);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(bean.getClass(), type);
        }
        return annotation;
    }

    private Binder getBinder() {
        if (this.binder == null) {
            this.binder = new Binder(getConfigurationPropertySources(),
                    getPropertySourcesPlaceholdersResolver(), conversionService,
                    getPropertyEditorInitializer());
        }
        return this.binder;
    }

    private Iterable<ConfigurationPropertySource> getConfigurationPropertySources() {
        return ConfigurationPropertySources.from(this.propertySources);
    }

    private PropertySourcesPlaceholdersResolver getPropertySourcesPlaceholdersResolver() {
        return new PropertySourcesPlaceholdersResolver(this.propertySources);
    }

    private Consumer<PropertyEditorRegistry> getPropertyEditorInitializer() {
        if (this.applicationContext instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.applicationContext)
                    .getBeanFactory()::copyRegisteredEditorsTo;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // We can't use constructor injection of the application context because
        // it causes eager factory bean initialization
        this.beanFactoryMetadata = this.applicationContext.getBean(
                ConfigurationBeanFactoryMetadata.BEAN_NAME,
                ConfigurationBeanFactoryMetadata.class);
    }
}
