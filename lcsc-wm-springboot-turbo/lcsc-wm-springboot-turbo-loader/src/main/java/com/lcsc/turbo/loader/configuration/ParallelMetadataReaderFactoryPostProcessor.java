package com.lcsc.turbo.loader.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 22:55
 */

class ParallelMetadataReaderFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        replaceMetadataReaderFactory(registry);
        configureConfigurationClassPostProcessor(registry);
    }

    private void replaceMetadataReaderFactory(BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(ParallelMetadataReaderFactoryApplicationContextInitializer.BEAN_NAME)) {
            registry.removeBeanDefinition(ParallelMetadataReaderFactoryApplicationContextInitializer.BEAN_NAME);
        }
        BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(ParallelLoadSharedMetadataReaderFactoryBean.class, ParallelLoadSharedMetadataReaderFactoryBean::new).getBeanDefinition();
        registry.registerBeanDefinition(ParallelMetadataReaderFactoryApplicationContextInitializer.BEAN_NAME, definition);
    }

    private void configureConfigurationClassPostProcessor(BeanDefinitionRegistry registry) {
        try {
            BeanDefinition definition = registry.getBeanDefinition(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
            definition.getPropertyValues().add("metadataReaderFactory", new RuntimeBeanReference(ParallelMetadataReaderFactoryApplicationContextInitializer.BEAN_NAME));
        } catch (NoSuchBeanDefinitionException ignored) {
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
