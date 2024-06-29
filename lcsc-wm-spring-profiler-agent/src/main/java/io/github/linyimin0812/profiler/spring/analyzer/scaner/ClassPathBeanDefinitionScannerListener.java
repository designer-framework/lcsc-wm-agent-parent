package io.github.linyimin0812.profiler.spring.analyzer.scaner;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractMethodInvokeDetailListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(String)
 **/
@MetaInfServices(EventListener.class)
public class ClassPathBeanDefinitionScannerListener extends AbstractMethodInvokeDetailListener {

    private static final String CLASS_NAME = "org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider";

    private static final String METHOD_NAME = "findCandidateComponents";

    private static final String[] METHOD_TYPES = new String[]{"java.lang.String"};

    @Override
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "ClassPathBeanDefinitionScanner扫包耗时";
    }

    @Override
    protected String listenClassName0() {
        return CLASS_NAME;
    }

    @Override
    protected String listenMethodName() {
        return METHOD_NAME;
    }

    @Override
    protected String[] listenMethodTypes() {
        return METHOD_TYPES;
    }

}
