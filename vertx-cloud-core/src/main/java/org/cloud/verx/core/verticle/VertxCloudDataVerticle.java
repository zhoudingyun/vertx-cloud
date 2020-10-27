package org.cloud.vertx.core.verticle;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

public abstract class VertxCloudDataVerticle extends VertxCloudVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxCloudDataVerticle.class);

    protected JsonObject verifyCloudDataConfig() {
        JsonObject cloudConfig = verifyCloudConfig();

        JsonObject dataConfig = cloudConfig.getJsonObject("data", null);
        if (dataConfig == null) {
            LOGGER.error(new RuntimeException("the property data is not configured, please check config.json."));
            System.exit(0);
        }

        return dataConfig;
    }
}
