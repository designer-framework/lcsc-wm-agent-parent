package io.github.linyimin0812.profiler.spring.analyzer.swagger;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractMethodInvokeDetailListener;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * {@link springfox.documentation.swagger2.annotations.EnableSwagger2}
 **/
@MetaInfServices(EventListener.class)
public class SwaggerAutoConfigurationListener extends AbstractMethodInvokeDetailListener {

    @Override
    protected String listenClassName0() {
        return "org.springframework.cloud.openfeign.FeignClientFactoryBean";
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
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "Swagger加载耗时";
    }

}
