package com.lcsc.turbo.loader.configuration;

import com.lcsc.turbo.common.utils.AsyncUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class ParallelClassPathScanningCandidateComponent extends ClassPathScanningCandidateComponentProvider {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private final ResourcePatternResolver resourcePatternResolver;

    private final MetadataReaderFactory metadataReaderFactory;

    public ParallelClassPathScanningCandidateComponent(MetadataReaderFactory metadataReaderFactory, ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
        this.metadataReaderFactory = metadataReaderFactory;
    }

    public static void main(String[] args) throws InterruptedException {
        int worker = Runtime.getRuntime().availableProcessors();
        int resourcesSize = 9;

        int forkSize = resourcesSize / worker;

        log.error("总文件个数: {}, 分片大小: {}", resourcesSize, forkSize);

        int remaining = resourcesSize;
        int currCursor = 0;
        while (currCursor < resourcesSize) {

            int currCursor_ = currCursor + forkSize;

            System.out.println(currCursor + "=" + (currCursor_ - 1));

            currCursor = currCursor_;

            remaining = remaining - forkSize;

            if (forkSize > remaining) {

                System.out.println((resourcesSize - remaining - 1) + "=" + (resourcesSize - 1));
                break;

            }

        }
    }

    @SneakyThrows
    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

            log.error("并行扫描{}, 总文件个数: {}", basePackage, resources.length);

            for (Resource resource : resources) {

                if (resource.isReadable()) {

                    AsyncUtils.submit(() -> {
                        try {
                            metadataReaderFactory.getMetadataReader(resource);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                }

            }

        } catch (IOException ex) {
            log.error("", ex);
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }

        return null;
    }

}
