package com.lcsc.turbo.loader.io;

import com.lcsc.turbo.common.thread.AsyncUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-03 23:20
 */
@Slf4j
public class ParallelPathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {

    public ParallelPathMatchingResourcePatternResolver(ClassLoader classLoader) {
        super(classLoader);
    }

    public static void main(String[] args) throws IOException {

        StopWatch stopWatch = new StopWatch();

//        stopWatch.start("并行");
//        ParallelPathMatchingResourcePatternResolver parallelPathMatching = new ParallelPathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
//        Resource[] resources = parallelPathMatching.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/*.*");
//        stopWatch.stop();

        stopWatch.start("串行");
        PathMatchingResourcePatternResolver parallelPathMatching_1 = new PathMatchingResourcePatternResolver();
        Resource[] resources_1 = parallelPathMatching_1.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/*.*");
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    @Override
    public Resource getResource(String location) {
        return super.getResource(location);
    }

    @Override
    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        return super.findPathMatchingResources(locationPattern);
    }

    @Override
    @SneakyThrows
    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, URL rootDirURL, String subPattern) throws IOException {
        return super.doFindPathMatchingJarResources(rootDirResource, rootDirURL, subPattern);
    }

    @Override
    @SneakyThrows
    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
        return AsyncUtils.submit(() -> super.doFindMatchingFileSystemResources(rootDir, subPattern)).get();
    }

    @Override
    @SneakyThrows
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
        return AsyncUtils.submit(() -> super.doFindPathMatchingFileResources(rootDirResource, subPattern)).get();
    }


    @SneakyThrows
    @Override
    protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        ConcurrentLinkedQueue<File> queue = new ConcurrentLinkedQueue<>();
        doRetrieveMatchingFiles0(fullPattern, dir, atomicInteger, queue);

        while (atomicInteger.get() != 0) {
            File file = queue.poll();
            if (file != null) {
                result.add(file);
            }
        }

    }

    protected void doRetrieveMatchingFiles0(String fullPattern, File dir, AtomicInteger atomicInteger, ConcurrentLinkedQueue<File> queue) {
        if (log.isTraceEnabled()) {
            log.trace("Searching directory [" + dir.getAbsolutePath() +
                    "] for files matching pattern [" + fullPattern + "]");
        }
        for (File content : listDirectory(dir)) {
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
                if (!content.canRead()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping subdirectory [" + dir.getAbsolutePath() + "] because the application is not allowed to read the directory");
                    }
                } else {
                    atomicInteger.incrementAndGet();
                    AsyncUtils.submit(() -> {
                        doRetrieveMatchingFiles0(fullPattern, content, atomicInteger, queue);
                        atomicInteger.decrementAndGet();
                    });
                }
            }
            if (getPathMatcher().match(fullPattern, currPath)) {
                queue.offer(content);
            }
        }
    }

}
