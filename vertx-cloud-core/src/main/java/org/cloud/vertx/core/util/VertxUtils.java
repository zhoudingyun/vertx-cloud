package org.cloud.vertx.core.util;

import io.vertx.servicediscovery.ServiceDiscoveryOptions;

public final class VertxUtils {
    public static ServiceDiscoveryOptions getServiceDiscoveryOptions() {
        ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions();
        serviceDiscoveryOptions.setAnnounceAddress("org.cloud.vertx");
        serviceDiscoveryOptions.setName("org_cloud_vertx");
        return serviceDiscoveryOptions;
    }
}
