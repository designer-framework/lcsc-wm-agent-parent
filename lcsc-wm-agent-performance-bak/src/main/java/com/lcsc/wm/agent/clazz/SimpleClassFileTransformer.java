package com.lcsc.wm.agent.clazz;

import com.alibaba.bytekit.asm.matcher.SimpleClassMatcher;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 17:42
 */
public abstract class SimpleClassFileTransformer implements ClassFileTransformer {

    private final SimpleClassMatcher simpleClassMatcher;

    /**
     * @param className "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"
     */
    public SimpleClassFileTransformer(String... className) {
        simpleClassMatcher = new SimpleClassMatcher(className);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        boolean isMatch = simpleClassMatcher.match(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        if (isMatch) {
            return transform(classfileBuffer);
        } else {
            return classfileBuffer;
        }
    }

    protected abstract byte[] transform(byte[] classfileBuffer);


}
