package com.cooldesktop.rmi.server.dump;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class DumpLoader implements Loader, ClassFileTransformer {

    private String className;
    private Instrumentation instrumentation;
    private volatile byte[] classByte;

    public DumpLoader(String className, Instrumentation instrumentation) {
        this.className = className;
        this.instrumentation = instrumentation;
    }

    @Override
    public boolean canLoad(String s) {
        return className.equals(s);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals(this.className.replace(".","/"))) {
            classByte = classfileBuffer;
        }
        return null;
    }

    @Override
    public byte[] load(String className) throws LoaderException {
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        instrumentation.addTransformer(DumpLoader.this, true);
        try {
            for (Class item : allLoadedClasses) {
                if (item.getName().equals(className)) {
                    instrumentation.retransformClasses(item);
                    return this.classByte;
                }
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(this);
        }
        return null;

    }
}
