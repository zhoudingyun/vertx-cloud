package org.cloud.vertx.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.cloud.vertx.core.util.VertxCloudUtils;

public abstract class VertxVerticle extends AbstractVerticle {

    /**
     * check config
     * if entries is null, you should todo in onFailure(e -> { //to do something })
     *
     * @param node Node example: vertx.cloud.data
     * @return JsonObject
     */
    protected JsonObject checkConfig(final String node) {
        JsonObject entries = VertxCloudUtils.checkConfig(config(), node);

        if (entries == null) {
            throw new RuntimeException("the property " + node + " is not config, please check config");
        }

        return entries;
    }

    /**
     * 验证组件的配置
     *
     * @return
     */
    protected abstract JsonObject verifyComponentConfig();

    /**
     * getServiceDiscovery
     *
     * @return ServiceDiscovery
     */
    protected ServiceDiscovery getServiceDiscovery() {
        return ServiceDiscovery.create(vertx, VertxCloudUtils.getServiceDiscoveryOptions());
    }
}
