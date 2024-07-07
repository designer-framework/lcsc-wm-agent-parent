package io.github.linyimin0812.profiler.spring.analyzer.aspectj;

import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractInvokeDetailListener;

public class AspectjListener extends AbstractInvokeDetailListener {

    private static final String[] CLASS = new String[]{
            "org.aspectj.apache.bcel.util.ClassLoaderRepository",
            "org.aspectj.apache.bcel.util.NonCachingClassLoaderRepository"
    };

    private static final String METHOD_NAME = "loadClass";

    private static final String[] METHOD_TYPES = new String[]{"java.lang.String"};

    @Override
    protected String[] listenClassName() {
        return CLASS;
    }

    @Override
    protected String listenMethodName() {
        return METHOD_NAME;
    }

    @Override
    protected String[] listenMethodTypes() {
        return METHOD_TYPES;
    }

    @Override
    protected String getFullyQualifiedName(AtEnterEvent atEnterEvent) {
        return atEnterEvent.clazz.getName() + "." + listenMethodName();
    }

    @Override
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "Aop加载耗时[加载类]";
    }

}
