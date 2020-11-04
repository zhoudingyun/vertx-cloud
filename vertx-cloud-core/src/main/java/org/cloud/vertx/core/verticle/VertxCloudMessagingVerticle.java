package org.cloud.vertx.core.verticle;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

public abstract class VertxCloudMessagingVerticle extends VertxCloudVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxCloudMessagingVerticle.class);
    protected JsonObject verifyCloudMessagingConfig() {
        JsonObject cloudConfig = verifyCloudConfig();

        JsonObject messagingConfig = cloudConfig.getJsonObject("messaging", null);
        if (messagingConfig == null) {
            LOGGER.error(new RuntimeException("the property messaging is not configured, please check config.json."));
            System.exit(1);
        }

        return messagingConfig;
    }
}
