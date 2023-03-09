package com.sample.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author 82196
 */
public class CommonTransformer implements ClassFileTransformer{

    private String targetClassName;

    private ClassLoader targetClassLoader;

    public CommonTransformer(String targetClassName, ClassLoader targetClassLoader) {
        this.targetClassName = targetClassName;
        this.targetClassLoader = targetClassLoader;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;

        String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/");

        if (!className.equals(finalTargetClassName)) {
            return byteCode;
        }

        if (className.equals(finalTargetClassName) && loader.equals(targetClassLoader)) {
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(targetClassName);

                //增强所有方法
                CtMethod[] ms = cc.getDeclaredMethods();
                for (int i = 0; i < ms.length; i++) {
                    CtMethod m = ms[i];
                    System.out.println("[Agent] transform method -> " + m.getName());
                    m.addLocalVariable("startTime",CtClass.longType);
                    m.insertBefore("{startTime=System.currentTimeMillis();}");

                    StringBuilder endBlock = new StringBuilder();
                    m.addLocalVariable("endTime",CtClass.longType);
                    m.addLocalVariable("opTime",CtClass.longType);
                    endBlock.append("endTime = System.currentTimeMillis();");
                    endBlock.append("opTime = endTime-startTime;");
                    endBlock.append("System.out.println(\"["+ cc.getName() + "] method -> " + m.getName() + " completed in:\" + opTime + \" ms!\");");
                    m.insertAfter(endBlock.toString());
                }

                byteCode = cc.toBytecode();
                cc.detach();
            } catch (Throwable e) {
                System.out.println("Exception" + e);
            }
        }
        return byteCode;
    }
}
