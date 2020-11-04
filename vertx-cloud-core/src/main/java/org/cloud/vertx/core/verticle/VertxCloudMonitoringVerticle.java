package org.cloud.vertx.core.verticle;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * 监控
 *
 * @author frank
 */
public class VertxCloudMonitoringVerticle extends VertxCloudVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxCloudMonitoringVerticle.class);

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject cloudConfig = verifyCloudConfig();

        JsonObject monitoringConfig = cloudConfig.getJsonObject("monitoring", null);
        if (monitoringConfig == null) {
            LOGGER.error(new RuntimeException("the property monitoring is not configured, please check config.json."));
            System.exit(1);
        }

        return monitoringConfig;
    }
}
