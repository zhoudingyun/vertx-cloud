package org.cloud.vertx.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VertxBeanUtils {
    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap();

    public static Object put(String name, Object instance) {
        return INSTANCES.put(name, instance);
    }

    public static Object putIfAbsent(String name, Object instance) {
        return INSTANCES.putIfAbsent(name, instance);
    }

    public static Object remove(String name) {
        return INSTANCES.remove(name);
    }

    public static <V> V get(String name) {
        return (V) INSTANCES.get(name);
    }

    public static <V> V getOrDefault(String name, Object instance) {
        return (V) INSTANCES.getOrDefault(name, instance);
    }
}
