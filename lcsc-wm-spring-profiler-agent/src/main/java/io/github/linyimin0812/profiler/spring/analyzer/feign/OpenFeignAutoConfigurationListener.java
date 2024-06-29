package io.github.linyimin0812.profiler.spring.analyzer.feign;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractComponentInvokeDetailListener;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * {@link org.springframework.cloud.openfeign.EnableFeignClients }
 **/
@MetaInfServices(EventListener.class)
public class OpenFeignAutoConfigurationListener extends AbstractComponentInvokeDetailListener {

    @Override
    protected String[] listenClassName() {
        return new String[]{"org.springframework.cloud.openfeign.FeignClientFactoryBean"};
    }

    @Override
    protected String listenMethodName() {
        return "getObject";
    }

    @Override
    protected String[] listenMethodTypes() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    protected String getInvokeCountMethodAlias(AtEnterEvent atEnterEvent) {
        return "OpenFeign加载耗时";
    }

}