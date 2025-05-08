package io.littlehorse.quarkus.deployment.processors;

import java.lang.reflect.Method;

public class LHClassLoader {

    private final Class<?> loadedClass;

    private LHClassLoader(String className) {
        try {
            this.loadedClass = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static LHClassLoader load(String className) {
        return new LHClassLoader(className);
    }

    public Class<?> getLoadedClass() {
        return loadedClass;
    }

    public Method loadMethod(String name, Class<?>... parameterTypes) {
        try {
            return loadedClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
