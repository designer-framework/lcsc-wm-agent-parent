package com.lcsc.turbo.loader.configuration;

import com.lcsc.turbo.common.utils.AsyncUtils;
import com.lcsc.turbo.loader.properties.ParallelLoadClassResourcesProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 22:56
 */
@Slf4j
public class ParallelLoadSharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, ApplicationListener<ContextRefreshedEvent> {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

    @Setter
    private ResourceLoader resourceLoader;

    @Setter
    private Environment environment;

    /**
     * @param classLoader the owning class loader
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
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
        if (initialized.compareAndSet(false, true)) {

            ParallelClassPathScanningCandidateComponent scanningCandidateComponent = new ParallelClassPathScanningCandidateComponent(environment, resourceLoader);
            scanningCandidateComponent.setResourceLoader(resourceLoader);
            scanningCandidateComponent.setMetadataReaderFactory(metadataReaderFactory);

            //
            ParallelLoadClassResourcesProperties properties = getParallelLoadClassResourcesProperties();
            CountDownLatch countDownLatch = new CountDownLatch(properties.getScanPackages().size());

            StopWatch stopWatch = new StopWatch();
            for (String preScanPath : properties.getScanPackages()) {

                //并行扫描
                if (properties.isEnabled()) {

                    stopWatch.start("并行扫包耗时");
                    AsyncUtils.submit(() -> {
                        try {
                            scanningCandidateComponent.findCandidateComponents(preScanPath);
                        } catch (Exception ignored) {
                            //
                        } finally {
                            countDownLatch.countDown();
                        }
                    });

                    countDownLatch.await();
                    stopWatch.stop();

                    //串行扫描
                } else {

                    try {
                        stopWatch.start(preScanPath);
                        countDownLatch.countDown();
                        scanningCandidateComponent.findCandidateComponents(preScanPath);
                        stopWatch.stop();
                    } catch (Exception ignored) {
                        //
                    }

                    countDownLatch.await();

                }
            }
            log.error("\n并行扫包-结束{}, {}", properties.getScanPackages(), stopWatch.prettyPrint());

        }
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

    /**
     * 扫包配置
     *
     * @return
     */
    private ParallelLoadClassResourcesProperties getParallelLoadClassResourcesProperties() {
        return Binder.get(environment)
                .bind(ConfigurationPropertyName.of("spring.turbo.loader"), Bindable.of(ParallelLoadClassResourcesProperties.class))
                .orElseGet(ParallelLoadClassResourcesProperties::new);
    }

}
