package com.lcsc.turbo.loader.configuration;

import com.lcsc.turbo.common.utils.TurboRunnable;
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
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 22:56
 */
@Slf4j
public class ParallelLoadSharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, BeanClassLoaderAware, EnvironmentAware, ResourceLoaderAware, ApplicationListener<ContextRefreshedEvent> {

    @Setter
    private Environment environment;

    @Setter
    private ResourceLoader resourceLoader;

    @Setter
    private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

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

        //
        ParallelLoadClassResourcesProperties properties = Binder.get(environment)
                .bind(ConfigurationPropertyName.of("spring.turbo.loader"), Bindable.of(ParallelLoadClassResourcesProperties.class))
                .orElseGet(ParallelLoadClassResourcesProperties::new);

        if (properties.isEnabled()) {
            //

            ClassPathScanningCandidateComponentProvider scanningCandidateComponent = new ClassPathScanningCandidateComponentProvider(false, environment);
            scanningCandidateComponent.setResourceLoader(resourceLoader);
            scanningCandidateComponent.setMetadataReaderFactory(metadataReaderFactory);

            for (String scanPackage : properties.getScanPackages()) {

                List<TurboRunnable.TRunnable> list = new ArrayList<>();

                try {
                    String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanPackage) + "/**/*.class";
                    Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);

                    for (Resource resource : resources) {

                        list.add(() -> {
                            metadataReaderFactory.getMetadataReader(resource);
                        });

                    }

                } catch (IOException e) {

                    throw new RuntimeException(e);

                }

                TurboRunnable turboRunnable = new TurboRunnable(list);
                turboRunnable.run();
                turboRunnable.completion();

            }

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

}
