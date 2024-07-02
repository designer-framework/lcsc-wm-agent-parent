package com.lcsc.turbo.loader;

import com.lcsc.turbo.common.utils.AsyncUtils;
import lombok.Setter;
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
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.*;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
            BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(PreLoadSharedMetadataReaderFactoryBean.class, PreLoadSharedMetadataReaderFactoryBean::new).getBeanDefinition();
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
        static class PreLoadSharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, ApplicationListener<ContextRefreshedEvent> {

            private final ReadWriteLock lock = new ReentrantReadWriteLock();
            private final Lock readLock = lock.readLock();
            private final Lock writeLock = lock.writeLock();
            private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

            @Setter
            private ResourceLoader resourceLoader;

            @Setter
            private Environment environment;

            @Setter
            private AtomicBoolean initialized = new AtomicBoolean(false);

            /**
             * @param classLoader the owning class loader
             */
            @Override
            public void setBeanClassLoader(ClassLoader classLoader) {
                metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(classLoader);

                if (initialized.compareAndSet(false, true)) {

                    ParallelClassPathScanningCandidateComponent scanningCandidateComponent = new ParallelClassPathScanningCandidateComponent(environment);
                    scanningCandidateComponent.setResourceLoader(resourceLoader);
                    scanningCandidateComponent.setMetadataReaderFactory(metadataReaderFactory);

                    for (String preScanPath : resolvePreScanPath()) {
                        readLock.lock();
                        AsyncUtils.submit(() -> {
                            try {
                                scanningCandidateComponent.findCandidateComponents(preScanPath);
                            } catch (Exception e) {
                                readLock.unlock();
                            } finally {
                                readLock.unlock();
                            }
                            return null;
                        });
                    }

                }

            }

            /**
             * 待扫描的包
             *
             * @return
             */
            private List<String> resolvePreScanPath() {
                return
                        (List<String>) Binder.get(environment)
                                .bind("defaultScan", Bindable.of(ResolvableType.forType(new ParameterizedTypeReference<List<String>>() {
                                })))
                                .orElse(Arrays.asList("com.lcsc"));
            }

            /**
             * 只会被读取一次
             *
             * @return
             * @throws Exception
             */
            @Override
            public ConcurrentReferenceCachingMetadataReaderFactory getObject() throws Exception {
                writeLock.lock();
                try {
                    return metadataReaderFactory;
                } finally {
                    writeLock.unlock();
                }
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

