package com.lcsc.turbo.loader.configuration;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 22:56
 */
@Slf4j
public class ParallelLoadSharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, BeanClassLoaderAware, EnvironmentAware, ApplicationListener<ContextRefreshedEvent> {

    @Setter
    private ClassLoader classLoader;

    @Setter
    private Environment environment;

    @Setter
    private MetadataReaderFactoryFactory metadataReaderFactoryFactory;

    /**
     * @param classLoader the owning class loader
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        metadataReaderFactoryFactory = new MetadataReaderFactoryFactory(classLoader, new ConcurrentReferenceCachingMetadataReaderFactory(classLoader));
        this.classLoader = classLoader;
    }

    /**
     * 只会被读取一次
     *
     * @return
     * @throws Exception
     */
    @Override
    public ConcurrentReferenceCachingMetadataReaderFactory getObject() throws Exception {
        return metadataReaderFactoryFactory.createMetadataReaderFactory(environment, classLoader);
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
        metadataReaderFactoryFactory.clearCache(classLoader);
    }

}
