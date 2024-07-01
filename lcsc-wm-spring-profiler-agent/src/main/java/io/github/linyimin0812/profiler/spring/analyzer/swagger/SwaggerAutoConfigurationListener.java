package io.github.linyimin0812.profiler.spring.analyzer.swagger;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractComponentInvokeDetailListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * {@link springfox.documentation.swagger2.annotations.EnableSwagger2}
 **/
@MetaInfServices(EventListener.class)
public class SwaggerAutoConfigurationListener extends AbstractComponentInvokeDetailListener {

    @Override
    protected String[] listenClassName() {
        return new String[]{"springfox.documentation"};
    }

    @Override
    protected String getFullyQualifiedName(AtEnterEvent atEnterEvent) {
        return "";
    }

    @Override
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "Swagger";
    }

}
