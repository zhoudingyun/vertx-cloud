package org.cloud.vertx.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

public abstract class VertxCloudVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxCloudVerticle.class);
    protected JsonObject cloudConfig;

    protected JsonObject verifyCloudConfig() {
        JsonObject config = config();
        JsonObject vertxConfig = config.getJsonObject("vertx", null);
        if (vertxConfig == null) {
            LOGGER.error(new RuntimeException("the property vertx is not configured, please check config.json."));
            System.exit(0);
        }
        cloudConfig = vertxConfig.getJsonObject("cloud", null);
        if (cloudConfig == null) {
            LOGGER.error(new RuntimeException("the property cloud is not configured, please check config.json."));
            System.exit(0);
        }

        return cloudConfig;
    }

    protected abstract JsonObject verifyComponentConfig();
}