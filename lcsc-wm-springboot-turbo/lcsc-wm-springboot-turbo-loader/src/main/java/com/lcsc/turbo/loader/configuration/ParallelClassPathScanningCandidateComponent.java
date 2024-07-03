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
import java.util.concurrent.CountDownLatch;

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

            int worker = Runtime.getRuntime().availableProcessors();
            int resourcesSize = resources.length;

            int forkSize = resourcesSize / worker;

            if (forkSize < 1000) {
                log.error("跳过并行扫描{}, 总文件个数: {}, 分片大小: {}", basePackage, resourcesSize, forkSize);
                return null;
            } else {
                log.error("启动并行扫描{}, 总文件个数: {}, 分片大小: {}", basePackage, resourcesSize, forkSize);
            }

            int currCursor = 0;
            int remaining = resourcesSize;
            CountDownLatch countDownLatch01 = new CountDownLatch(((resourcesSize % worker) == 0) ? worker : worker + 1);
            while (currCursor < resourcesSize) {

                int cursorStart = currCursor;
                int cursorEnd = currCursor + forkSize;

                AsyncUtils.submit(() -> {
                    System.out.println(cursorStart + "=" + (cursorEnd - 1));
                    resolveResource(resources, cursorStart, cursorEnd - 1);
                    countDownLatch01.countDown();
                    System.out.println(countDownLatch01.getCount());
                });

                currCursor = cursorEnd;

                remaining = remaining - forkSize;

                if (forkSize > remaining) {

                    int cursorStart_ = resourcesSize - remaining;
                    AsyncUtils.submit(() -> {
                        System.out.println(cursorStart_ + "=" + (resourcesSize - 1));
                        resolveResource(resources, cursorStart_, resourcesSize - 1);
                        countDownLatch01.countDown();
                        System.out.println(countDownLatch01.getCount());
                    });
                    break;

                }

            }
            countDownLatch01.await();

        } catch (IOException ex) {
            log.error("", ex);
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }

        return null;
    }

    protected void resolveResource(Resource[] resources, int start, int end) {
        for (int i = start; i <= end; i++) {

            Resource resource = resources[i];

            if (resource.isReadable()) {

                try {
                    metadataReaderFactory.getMetadataReader(resource);
                } catch (Throwable ex) {
                    log.error("", ex);
                    throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
                }

            }

        }
    }

}