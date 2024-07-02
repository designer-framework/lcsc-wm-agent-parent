package com.lcsc.turbo.loader;

import com.lcsc.turbo.common.utils.AsyncUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

class ParallelClassPathScanningCandidateComponent extends ClassPathScanningCandidateComponentProvider {

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

            int resourcesSize = resources.length;
            int worker = Runtime.getRuntime().availableProcessors();
            int peek = resourcesSize / worker;
            int preCursor = 0;
            int currCursor = 0;
            for (int i = 1; i < peek; i++) {
                int cursor_ = preCursor;
                AsyncUtils.submit(() -> {
                    resolveResource(resources, cursor_, i * peek);
                });
            }

            boolean traceEnabled = logger.isTraceEnabled();
            for (Resource resource : resources) {
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
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return Collections.emptySet();
    }

    protected ResourcePatternResolver resolveResource(Resource[] resources, int start, int end) {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
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
