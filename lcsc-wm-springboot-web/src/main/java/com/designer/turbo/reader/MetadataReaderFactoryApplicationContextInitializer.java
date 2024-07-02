package com.designer.turbo.reader;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.*;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 01:51
 * @see org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer
 */

public class MetadataReaderFactoryApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    /**
     * @see org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer#BEAN_NAME
     */
    public static final String BEAN_NAME = "org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addBeanFactoryPostProcessor(new MetadataReaderFactoryMergedBeanDefinitionPostProcessor());
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public static class MetadataReaderFactoryMergedBeanDefinitionPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            register(registry);
            configureConfigurationClassPostProcessor(registry);
        }

        private void register(BeanDefinitionRegistry registry) {
            replaceMetadataReaderFactory(registry);
        }

        private void replaceMetadataReaderFactory(BeanDefinitionRegistry registry) {
            if (registry.containsBeanDefinition(BEAN_NAME)) {
                registry.removeBeanDefinition(BEAN_NAME);
            }
            BeanDefinition definition = BeanDefinitionBuilder
                    .genericBeanDefinition(PreLoadSharedMetadataReaderFactoryBean.class, PreLoadSharedMetadataReaderFactoryBean::new)
                    .getBeanDefinition();
            registry.registerBeanDefinition(BEAN_NAME, definition);
        }

        private void configureConfigurationClassPostProcessor(BeanDefinitionRegistry registry) {
            try {
                BeanDefinition definition = registry.getBeanDefinition(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
                definition.getPropertyValues().add("metadataReaderFactory", new RuntimeBeanReference(BEAN_NAME));
            } catch (NoSuchBeanDefinitionException ignored) {
            }
        }

        /**
         * {@link FactoryBean} to create the shared {@link MetadataReaderFactory}.
         */
        static class PreLoadSharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, BeanClassLoaderAware, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

            private final Object lock = new Object();

            private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

            @Setter
            private ApplicationContext applicationContext;

            @SneakyThrows
            @Override
            public void setBeanClassLoader(ClassLoader classLoader) {
                synchronized (lock) {
                    metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(classLoader);
                    parallelPreLoad(
                            applicationContext.getEnvironment().getProperty("defaultScan", String[].class, new String[]{"com.designer"})
                    );
                }
            }

            /**
             * @param classLoader the owning class loader
             * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents(java.lang.String)
             */
            private void parallelPreLoad(String[] defaultScan) throws IOException {
                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
                pathMatchingResourcePatternResolver.getResources("");
            }

            @Override
            public ConcurrentReferenceCachingMetadataReaderFactory getObject() throws Exception {
                return metadataReaderFactory;
            }

            @Override
            public Class<?> getObjectType() {
                return CachingMetadataReaderFactory.class;
            }

            @Override
            public boolean isSingleton() {
                return true;
            }

            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                metadataReaderFactory.clearCache();
            }

        }

    }

}

