package com.lcsc.turbo.loader.configuration;

import com.lcsc.turbo.common.utils.AsyncUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class ParallelClassPathScanningCandidateComponent extends ClassPathScanningCandidateComponentProvider {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private final ResourceLoader resourceLoader;

    public ParallelClassPathScanningCandidateComponent(Environment environment, ResourceLoader resourceLoader) {
        super(false, environment);
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);

            int worker = Runtime.getRuntime().availableProcessors();
            int resourcesSize = resources.length;

            int forkSize = resourcesSize / worker;

            if (forkSize < 500) {
                log.error("跳过并行扫描{}, 总文件个数: {}, 分片大小: {}", basePackage, resourcesSize, forkSize);
                return null;
            } else {
                log.error("启动并行扫描{}, 总文件个数: {}, 分片大小: {}", basePackage, resourcesSize, forkSize);
            }

            int preCursor = 0;
            do {

                int preCursor_ = preCursor;
                int currCursor_ = (preCursor_ + forkSize) - 1;

                AsyncUtils.submit(() -> {
                    resolveResource(resources, preCursor_, currCursor_);
                });

                preCursor = preCursor_ + forkSize;

            } while (preCursor < resourcesSize);

            if (preCursor != resourcesSize) {

                int preCursor_ = preCursor;

                AsyncUtils.submit(() -> {
                    resolveResource(resources, (preCursor_ - forkSize), (resourcesSize - 1));
                });

            }


        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }

        return null;
    }

    protected void resolveResource(Resource[] resources, int start, int end) {

        boolean traceEnabled = logger.isTraceEnabled();
        for (int i = start; i < end; i++) {

            Resource resource = resources[i];

            if (traceEnabled) {
                logger.trace("Scanning " + resource);
            }

            if (resource.isReadable()) {

                try {
                    getMetadataReaderFactory().getMetadataReader(resource);
                } catch (Throwable ex) {
                    throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
                }

            } else {

                if (traceEnabled) {
                    logger.trace("Ignored because not readable: " + resource);
                }

            }

        }
    }

    /**
     * @return
     * @see super#setResourceLoader(ResourceLoader)
     * @see super#getResourcePatternResolver()
     */
    protected ResourcePatternResolver getResourcePatternResolver() {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

}
