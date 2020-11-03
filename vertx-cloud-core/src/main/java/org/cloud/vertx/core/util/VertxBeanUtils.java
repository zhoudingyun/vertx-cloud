package org.cloud.vertx.core.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class VertxBeanUtils {
    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap();

    public static Object put(String name, Object instance) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(instance);
        return INSTANCES.put(name, instance);
    }

    public static Object putIfAbsent(String name, Object instance) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(instance);
        return INSTANCES.putIfAbsent(name, instance);
    }

    public static Object remove(String name) {
        Objects.requireNonNull(name);
        return INSTANCES.remove(name);
    }

    public static <V> V get(String name) {
        Objects.requireNonNull(name);
        return (V) INSTANCES.get(name);
    }

    public static <V> V getOrDefault(String name, Object instance) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(instance);
        return (V) INSTANCES.getOrDefault(name, instance);
    }

    public static Object put(Class clazz, Object instance) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(instance);
        return put(clazz.getName(), instance);
    }

    public static Object putIfAbsent(Class clazz, Object instance) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(instance);
        return putIfAbsent(clazz.getName(), instance);
    }

    public static Object remove(Class clazz) {
        Objects.requireNonNull(clazz);
        return remove(clazz.getName());
    }

    public static <V> V get(Class clazz) {
        Objects.requireNonNull(clazz);
        return get(clazz.getName());
    }

    public static <V> V getOrDefault(Class clazz, Object instance) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(instance);
        return getOrDefault(clazz.getName(), instance);
    }
}
