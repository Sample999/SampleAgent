package com.sample.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author 82196
 */
public class SampleInstrumentationAgent {

    /**
     * 增强所有类
     * @param instrumentation
     */
    public static void transformClass(Instrumentation instrumentation) {
        Class<?> targetCls;
        ClassLoader targetClassLoader;
        // 迭代所有加载的类
        for(Class<?> clazz: instrumentation.getAllLoadedClasses()) {
            targetCls = clazz;
            targetClassLoader = targetCls.getClassLoader();
            if("AtmApplication".equals(targetCls) || null == targetClassLoader){
                return;
            }
            transform(targetCls, targetClassLoader, instrumentation);
        }
    }

    /**
     * 增强指定类
     * @param className
     * @param instrumentation
     */
    public static void transformClass(String className, Instrumentation instrumentation) {
        Class<?> targetCls;
        ClassLoader targetClassLoader;
        try {
            targetCls = Class.forName(className);
            targetClassLoader = targetCls.getClassLoader();
            transform(targetCls, targetClassLoader, instrumentation);
            return;
        } catch (Exception ex) {
            System.err.println("Class [{}] not found with Class.forName");
        }
        // 否则迭代所有加载的类并找到我们想要的
        for(Class<?> clazz: instrumentation.getAllLoadedClasses()) {
            if(clazz.getName().equals(className)) {
                targetCls = clazz;
                targetClassLoader = targetCls.getClassLoader();
                transform(targetCls, targetClassLoader, instrumentation);
                return;
            }
            }
            throw new RuntimeException("Failed to find class [" + className + "]");
    }

    private static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation) {
        CommonTransformer dt = new CommonTransformer(clazz.getName(), classLoader);
        instrumentation.addTransformer(dt, true);
        try {
            instrumentation.retransformClasses(clazz);
        } catch (Exception ex) {
            throw new RuntimeException("Transform failed for class: [" + clazz.getName() + "]", ex);
        }
    }
}
