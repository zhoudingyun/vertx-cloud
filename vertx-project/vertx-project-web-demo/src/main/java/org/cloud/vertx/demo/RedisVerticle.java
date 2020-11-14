package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.spi.ServicePublisher;
import io.vertx.servicediscovery.types.RedisDataSource;

public class RedisVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        JsonObject redisConfig = new JsonObject();
        redisConfig.put("connectionString", "redis://172.18.20.76:6379/0");
        ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions();
        serviceDiscoveryOptions.setAnnounceAddress("/");
        serviceDiscoveryOptions.setName("thirdparty");

        ServiceDiscovery discovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions);

        Record record = RedisDataSource.createRecord(
                "some-redis-data-source-service", // The service name
                redisConfig, // The location
                new JsonObject().put("some-metadata", "some-value") // Some metadata
        );

        discovery.publish(record).onSuccess(aRecord -> {

        }).onFailure(throwable -> {

        });

    }

    @Override
    public void stop() throws Exception {
    }
}
