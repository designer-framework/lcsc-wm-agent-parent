package com.lcsc.turbo.loader.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-02 01:51
 * @see org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer
 */

public class ParallelMetadataReaderFactoryApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    /**
     * @see org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer#BEAN_NAME
     */
    static final String BEAN_NAME = "org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addBeanFactoryPostProcessor(new ParallelMetadataReaderFactoryPostProcessor());
    }

    @Override
    public int getOrder() {
        return 1;
    }

}

