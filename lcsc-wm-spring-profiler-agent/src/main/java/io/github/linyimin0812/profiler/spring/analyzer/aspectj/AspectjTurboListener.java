package io.github.linyimin0812.profiler.spring.analyzer.aspectj;

import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.spring.BaseEventListener;
import lombok.SneakyThrows;
import org.aspectj.apache.bcel.util.Repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectjTurboListener extends BaseEventListener {

    private static final String[] CLASS = new String[]{"org.aspectj.weaver.reflect.Java15AnnotationFinder"};
    private static final String METHOD_NAME = "setClassLoader";
    private static final String[] METHOD_TYPES = new String[]{"java.lang.ClassLoader"};
    private static final List<Event.Type> events = Arrays.asList(Event.Type.AT_EXIT);

    public static Map<ClassLoader, Repository> aspectjCache = new HashMap<>();

    public static Map<String, Class<?>> classCache = new HashMap<>();

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

    @SneakyThrows
    @Override
    protected void atExit(InvokeEvent event) {
        Object target = event.target;
        ClassLoader classLoader = event.clazz.getClassLoader();

        Class<?> java15AnnotationFinderClass = forName(CLASS[0], classLoader);

        //字段classLoaderRef
        Field classLoaderRef = java15AnnotationFinderClass.getDeclaredField("classLoaderRef");
        classLoaderRef.setAccessible(true);

        //字段bcelRepository
        Field bcelRepository = java15AnnotationFinderClass.getDeclaredField("bcelRepository");
        bcelRepository.setAccessible(true);

        //重置bcelRepository
        bcelRepository.set(target, instanceClassLoaderRepository(classLoaderRef.get(target), classLoader));
    }

    private Object instanceClassLoaderRepository(Object classLoaderRef, ClassLoader classLoader) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //
        Class<?> classLoaderReferenceClass = forName("org.aspectj.apache.bcel.util.ClassLoaderRepository", classLoader);
        //
        Constructor<?> bcelRepositoryConstruct = classLoaderReferenceClass.getConstructor(forName("org.aspectj.apache.bcel.util.ClassLoaderReference", classLoader));
        return bcelRepositoryConstruct.newInstance(classLoaderRef);
    }

    private Class<?> forName(String clazz, ClassLoader classLoader) throws ClassNotFoundException {
        if (!classCache.containsKey(clazz)) {
            classCache.put(clazz, Class.forName(clazz, true, classLoader));
        }
        return classCache.get(clazz);
    }

    @Override
    public List<Event.Type> listen() {
        return events;
    }

}
