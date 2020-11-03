package org.cloud.vertx.core.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class VertxBeanUtils {
    private static final Map<Object, Object> INSTANCES = new ConcurrentHashMap();

    public static <K, V> void put(K name, V instance) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(instance);
        INSTANCES.put(name, instance);
    }

    public static <K, V> void putIfAbsent(K name, V instance) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(instance);
        INSTANCES.putIfAbsent(name, instance);
    }

    public static <K> void remove(K name) {
        Objects.requireNonNull(name);
        INSTANCES.remove(name);
    }

    public static <K, V> V get(K name) {
        Objects.requireNonNull(name);
        return (V) INSTANCES.get(name);
    }

    public static <K, V> V getOrDefault(K name, V instance) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(instance);
        return (V) INSTANCES.getOrDefault(name, instance);
    }
}
