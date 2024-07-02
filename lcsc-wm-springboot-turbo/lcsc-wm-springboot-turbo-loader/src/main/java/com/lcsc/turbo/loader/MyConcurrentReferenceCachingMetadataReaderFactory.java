package com.lcsc.turbo.loader;

import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;

public class MyConcurrentReferenceCachingMetadataReaderFactory extends ConcurrentReferenceCachingMetadataReaderFactory {

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return super.getMetadataReader(resource);
    }

    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        return super.getMetadataReader(className);
    }

}
