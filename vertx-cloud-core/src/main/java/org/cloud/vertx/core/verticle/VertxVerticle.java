package org.cloud.vertx.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.cloud.vertx.core.util.VertxUtils;

import java.util.Objects;

public abstract class VertxVerticle extends AbstractVerticle {

    /**
     * check config
     * if entries is null, you should todo in onFailure(e -> { //to do something })
     *
     * @param node Node example: vertx.cloud.data
     * @return JsonObject
     */
    protected JsonObject checkConfig(final String node) {
        Objects.requireNonNull(node);
        JsonObject config = config();
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

        if (entries == null) {
            throw new RuntimeException("the property " + node + " is not config, please check config");
        }

        return entries;
    }

    protected abstract JsonObject verifyComponentConfig();

    /**
     * getServiceDiscovery
     *
     * @return ServiceDiscovery
     */
    protected ServiceDiscovery getServiceDiscovery() {
        return ServiceDiscovery.create(vertx, VertxUtils.getServiceDiscoveryOptions());
    }
}
