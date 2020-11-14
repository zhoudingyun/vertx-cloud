package org.cloud.vertx.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

public abstract class VertxCloudVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxCloudVerticle.class);
    protected JsonObject vertxConfig;
    protected JsonObject applicationConfig;
    protected JsonObject cloudConfig;
    protected static ServiceDiscovery serviceDiscovery;

    protected JsonObject verifyCloudConfig() {
        JsonObject config = config();

        vertxConfig = config.getJsonObject("vertx", null);
        if (vertxConfig == null) {
            LOGGER.error(new RuntimeException("the property vertx is not configured, please check config.json."));
            System.exit(1);
        }

        applicationConfig = vertxConfig.getJsonObject("application", null);
        if (applicationConfig == null) {
            LOGGER.error(new RuntimeException("the property vertx.application is not configured, please check config.json."));
            System.exit(1);
        }

        cloudConfig = vertxConfig.getJsonObject("cloud", null);
        if (cloudConfig == null) {
            LOGGER.error(new RuntimeException("the property vertx.cloud is not configured, please check config.json."));
            System.exit(1);
        }

        return cloudConfig;
    }

    protected abstract JsonObject verifyComponentConfig();

    protected ServiceDiscovery getServiceDiscovery() {
        if (serviceDiscovery != null) {
            return serviceDiscovery;
        } else {
            ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions();
            serviceDiscoveryOptions.setAnnounceAddress("/org/cloud/vertx");
            serviceDiscoveryOptions.setName("org_cloud_vertx");

            serviceDiscovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions);
            return serviceDiscovery;
        }
    }
}
