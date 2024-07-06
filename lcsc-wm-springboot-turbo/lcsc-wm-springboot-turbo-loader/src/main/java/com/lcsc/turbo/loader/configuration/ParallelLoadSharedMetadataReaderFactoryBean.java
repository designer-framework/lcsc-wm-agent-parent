package com.lcsc.turbo.loader.configuration;

import com.lcsc.turbo.common.thread.AsyncUtils;
import com.lcsc.turbo.common.thread.Callback;
import com.lcsc.turbo.common.thread.TCallable;
import com.lcsc.turbo.loader.properties.ParallelLoadClassResourcesProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.ClassLoaderRepository;
import org.aspectj.weaver.bcel.BcelWeakClassLoaderReference;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 22:56
 */
@Slf4j
public class ParallelLoadSharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, ApplicationListener<ContextRefreshedEvent> {

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    @Setter
    private static ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

    @Setter
    private Environment environment;
    @Setter
    private ClassLoader classLoader;
    @Setter
    private ResourceLoader resourceLoader;

    /**
     * @param classLoader the owning class loader
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(classLoader);
    }

    /**
     * 只会被读取一次
     *
     * @return
     * @throws Exception
     */
    @Override
    public ConcurrentReferenceCachingMetadataReaderFactory getObject() throws Exception {
        //
        ParallelLoadClassResourcesProperties properties = Binder.get(environment)
                .bind(ConfigurationPropertyName.of("spring.turbo.loader"), Bindable.of(ParallelLoadClassResourcesProperties.class))
                .orElseGet(ParallelLoadClassResourcesProperties::new);

        if (properties.isEnabled()) {

            //
            List<String> componentScans = new ArrayList<>();
            AnnotatedElementUtils.findAllMergedAnnotations(deduceMainApplicationClass(), ComponentScan.class)
                    .forEach(componentScan -> {
                        Collections.addAll(componentScans, componentScan.value());
                    });

            //
            ClassPathScanningCandidateComponentProvider componentProvider = getClassPathScanningCandidateComponentProvider();
            CountDownLatch scanFlag = new CountDownLatch(componentScans.size());
            componentScans.forEach(packageName -> {

                Callback<Void> callback = new Callback<Void>(packageName) {
                    @Override
                    public void onSuccess(Void result) {
                        scanFlag.countDown();
                    }
                };
                //
                AsyncUtils.submit(new TCallable<Void>(callback) {
                    @Override
                    public Void doCall() throws Exception {
                        componentProvider.findCandidateComponents(packageName);
                        return null;
                    }

                });

            });

            //

            ClassLoaderRepository classLoaderRepository = getClassLoaderRepository(classLoader);
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            CountDownLatch readFlag = new CountDownLatch(1);
            AtomicBoolean state = new AtomicBoolean(false);
            CyclicBarrier cyclicBarrier = new CyclicBarrier(componentScans.size(), readFlag::countDown);
            executorService.execute(() -> {

                try {

                    while (true) {

                        String pollClass = queue.peek();
                        if (pollClass != null) {

                            classLoaderRepository.loadClass(pollClass);

                        } else {

                            if (state.get()) {

                                String queueClass = queue.peek();
                                if (queueClass != null) {
                                    classLoaderRepository.loadClass(queueClass);
                                    continue;
                                } else {
                                    cyclicBarrier.await();
                                    break;
                                }

                            } else {
                                Thread.sleep(100);
                            }

                        }


                    }

                } catch (InterruptedException | ClassNotFoundException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }

            });


            //扫描完成
            scanFlag.await();
            state.compareAndSet(false, true);
            //读取完成
            readFlag.await();
            executorService.shutdown();
        }

        return metadataReaderFactory;
    }

    private ClassLoaderRepository getClassLoaderRepository(ClassLoader classLoader) {
        BcelWeakClassLoaderReference bcelWeakClassLoaderReference = new BcelWeakClassLoaderReference(classLoader);
        ClassLoaderRepository classLoaderRepository = new ClassLoaderRepository(bcelWeakClassLoaderReference) {
            @Override
            public JavaClass loadClass(String className) throws ClassNotFoundException {
                synchronized (className.intern()) {
                    return super.loadClass(className);
                }
            }
        };
        return classLoaderRepository;
    }

    private ClassPathScanningCandidateComponentProvider getClassPathScanningCandidateComponentProvider() {

        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return super.isCandidateComponent(beanDefinition);
            }

            @Override
            protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                queue.offer(classMetadata.getClassName());
                return false;
            }

        };

        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        componentProvider.setResourceLoader(resourcePatternResolver);
        componentProvider.setMetadataReaderFactory(metadataReaderFactory);

        return componentProvider;

    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
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
