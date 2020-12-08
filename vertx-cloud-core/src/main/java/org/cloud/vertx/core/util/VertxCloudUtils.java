package org.cloud.vertx.core.util;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

import java.util.Objects;

/**
 * Vertx cloud utils
 *
 * @author frank
 */
public final class VertxCloudUtils {

    /**
     * Get ServiceDiscoveryOptions
     *
     * @return ServiceDiscoveryOptions
     */
    public static ServiceDiscoveryOptions getServiceDiscoveryOptions() {
        ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions();
        serviceDiscoveryOptions.setAnnounceAddress("org.cloud.vertx");
        serviceDiscoveryOptions.setName("org_cloud_vertx");
        return serviceDiscoveryOptions;
    }

    /**
     * Check if the node is in configuration
     *
     * @param config Config
     * @param node   node
     * @return node
     */
    public static JsonObject checkConfig(final JsonObject config, final String node) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(node);
        String[] nodes = node.split("\\.");

        JsonObject entries = null;
        for (String key : nodes) {
            if (entries == null) {
                entries = config;
            }
            if (entries.containsKey(key)) {
                entries = entries.getJsonObject(key);
            } else {
                entries = null;
            }
        }

        return entries;
    }
}
