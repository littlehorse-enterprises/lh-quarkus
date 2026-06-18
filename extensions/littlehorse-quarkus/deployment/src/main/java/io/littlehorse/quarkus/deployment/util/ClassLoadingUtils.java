package io.littlehorse.quarkus.deployment.util;

public final class ClassLoadingUtils {

    private ClassLoadingUtils() {}

    public static Class<?> loadClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
