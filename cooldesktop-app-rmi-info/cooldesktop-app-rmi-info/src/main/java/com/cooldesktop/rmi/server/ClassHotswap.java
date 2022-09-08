package com.cooldesktop.rmi.server;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class ClassHotswap {
    private static Class<?> findClassByClassName(Instrumentation inst, String className) {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class classItem : allLoadedClasses) {
            if (classItem.getName().equals(className)) return classItem;
        }
        return null;
    }

    public   String hotswap(String className, Instrumentation inst, byte[] classFile) {
        Class<?> targetClass = findClassByClassName(inst, className);
        try {
            Class<?> aClass = targetClass == null ? Class.forName(className) : Class.forName(className, true, targetClass.getClassLoader());
            inst.redefineClasses(new ClassDefinition(aClass, classFile));
            return "替换成功";
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            e.printStackTrace();
            return "替换失败[" + e.getMessage() + "]";
        }
    }
}
