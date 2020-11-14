package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.impl.RedisClient;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.RedisDataSource;

import java.util.Arrays;

public class HttpServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions();
        serviceDiscoveryOptions.setAnnounceAddress("/");
        serviceDiscoveryOptions.setName("thirdparty");
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions);

        vertx.setPeriodic(1000, aLong -> {
            RedisDataSource.getRedisClient(discovery, new JsonObject().put("name", "some-redis-data-source-service"), ar -> {
                if (ar.succeeded()) {
                    Redis redis = ar.result();
                    RedisAPI.api(redis).set(Arrays.asList("test", System.currentTimeMillis()+""));
                    //ServiceDiscovery.releaseServiceObject(discovery, redis);
                }
            });
        });
    }

    @Override
    public void stop() throws Exception {
    }
}
