package com.lcsc.turbo.loader.configuration;

import com.lcsc.turbo.common.utils.AsyncUtils;
import com.lcsc.turbo.loader.properties.ParallelLoadClassResourcesProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Slf4j
class MetadataReaderFactoryFactory {

    private static final Map<ClassLoader, ConcurrentReferenceCachingMetadataReaderFactory> metadataReaderFactoryMap = new HashMap<>();

    private static final Set<ClassLoader> loaded = new HashSet<>();

    public MetadataReaderFactoryFactory(ClassLoader classLoader, ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory) {
        metadataReaderFactoryMap.computeIfAbsent(classLoader, classLoader_ -> metadataReaderFactory);
    }

    @SneakyThrows
    public synchronized ConcurrentReferenceCachingMetadataReaderFactory createMetadataReaderFactory(Environment environment, ClassLoader classLoader) {
        if (loaded.contains(classLoader)) {
            log.error("扫包数据已缓存: {}", classLoader);
            return metadataReaderFactoryMap.get(classLoader);
        }
        ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory = metadataReaderFactoryMap.get(classLoader);
        //
        ParallelClassPathScanningCandidateComponent scanningCandidateComponent = new ParallelClassPathScanningCandidateComponent(metadataReaderFactory, ResourcePatternUtils.getResourcePatternResolver(metadataReaderFactory.getResourceLoader()));
        scanningCandidateComponent.setEnvironment(environment);

        //
        ParallelLoadClassResourcesProperties properties = Binder.get(environment)
                .bind(ConfigurationPropertyName.of("spring.turbo.loader"), Bindable.of(ParallelLoadClassResourcesProperties.class))
                .orElseGet(ParallelLoadClassResourcesProperties::new);

        CountDownLatch countDownLatch = new CountDownLatch(properties.getScanPackages().size());

        //并行扫描
        if (properties.isEnabled()) {

            StopWatch stopWatch = new StopWatch("并行扫包总耗时");

            stopWatch.start();
            for (String preScanPath : properties.getScanPackages()) {

                AsyncUtils.submit(() -> {

                    try {

                        scanningCandidateComponent.findCandidateComponents(preScanPath);

                        log.error("\n包名->{}", preScanPath);

                    } catch (Exception e) {
                        log.error("扫包异常", e);
                        //
                    } finally {

                        countDownLatch.countDown();

                    }

                });

            }

            countDownLatch.await();
            stopWatch.stop();
            log.error("\n扫包结束{}, {}", properties.getScanPackages(), stopWatch.prettyPrint());


            //串行扫描
        } else {

            StopWatch stopWatch = new StopWatch("串行扫包统计");

            for (String preScanPath : properties.getScanPackages()) {

                try {

                    stopWatch.start(preScanPath);
                    scanningCandidateComponent.findCandidateComponents(preScanPath);
                    countDownLatch.countDown();
                    stopWatch.stop();

                    log.error("\n包名->{}", preScanPath);

                } catch (Exception e) {

                    log.error("扫包异常", e);

                }

            }

            countDownLatch.await();
            log.error("\n扫包结束{}, {}", properties.getScanPackages(), stopWatch.prettyPrint());

        }

        loaded.add(classLoader);
        log.error("缓存扫包数据: {}", classLoader);
        return metadataReaderFactory;
    }

    public void clearCache(ClassLoader classLoader) {
        metadataReaderFactoryMap.remove(classLoader);
    }

}