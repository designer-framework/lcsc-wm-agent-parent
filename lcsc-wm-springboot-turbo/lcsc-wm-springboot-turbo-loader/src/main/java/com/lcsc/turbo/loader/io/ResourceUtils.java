package com.lcsc.turbo.loader.io;

import com.lcsc.turbo.common.thread.AsyncUtils;
import com.lcsc.turbo.common.thread.Callback;
import com.lcsc.turbo.common.thread.TCallable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ResourceUtils {

    public static void doRead(MetadataReaderFactory metadataReaderFactory, Resource[] resource, String scanPackage) {
        StopWatch stopWatch = new StopWatch("文件扫描耗时");

        stopWatch.start(scanPackage);
        //任务数
        int jobNums = resource.length;
        //线程数
        int theadNum = Runtime.getRuntime().availableProcessors();
        //分片任务数
        int forkNum = jobNums / theadNum;

        List<TCallable<Object>> list = new ArrayList<>();
        Callback<Object> defaultCallback = new Callback<>();
        AtomicInteger atomicInteger = new AtomicInteger();
        //循环10次
        for (int i = 0; i <= theadNum - 1; i++) {
            if (i == 0) {

                atomicInteger.incrementAndGet();
                int start = i;
                int end = forkNum;
                list.add(new TCallable<Object>(defaultCallback) {
                    @Override
                    public Object doCall() throws Exception {
                        for (int a = start; a < end; a++) {
                            metadataReaderFactory.getMetadataReader(resource[a]);
                        }
                        atomicInteger.decrementAndGet();
                        return null;
                    }
                });

            } else if (i == theadNum - 1) {

                atomicInteger.incrementAndGet();
                int start = i * forkNum + 1;
                int end = jobNums - 1;
                list.add(new TCallable<Object>(defaultCallback) {
                    @Override
                    public Object doCall() throws Exception {
                        for (int a = start; a < end; a++) {
                            metadataReaderFactory.getMetadataReader(resource[a]);
                        }
                        atomicInteger.decrementAndGet();
                        return null;
                    }
                });

            } else {

                atomicInteger.incrementAndGet();
                int start = (i * forkNum + 1);
                int end = i * forkNum + forkNum;
                list.add(new TCallable<Object>(defaultCallback) {
                    @Override
                    public Object doCall() throws Exception {
                        for (int a = start; a < end; a++) {
                            metadataReaderFactory.getMetadataReader(resource[a]);
                        }
                        atomicInteger.decrementAndGet();
                        return null;
                    }
                });

            }

        }

        AsyncUtils.doInvokeAll(list);

        //是否执行完
        while (!atomicInteger.compareAndSet(0, -1)) {
            //ignored
        }

        stopWatch.stop();

    }

}
