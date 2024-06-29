package io.github.linyimin0812.profiler.spring.analyzer.nacos;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractMethodInvokeDetailListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * {@link com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration }
 **/
@MetaInfServices(EventListener.class)
public class NacosAutoConfigurationListener extends AbstractMethodInvokeDetailListener {

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        return super.filter(methodName, methodTypes);
    }

    @Override
    protected String listenClassName0() {
        return "com.alibaba.cloud.nacos";
    }

    @Override
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "Nacos加载耗时";
    }

    @Override
    protected String listenMethodName() {
        return null;
    }

    @Override
    protected String[] listenMethodTypes() {
        return null;
    }

}
