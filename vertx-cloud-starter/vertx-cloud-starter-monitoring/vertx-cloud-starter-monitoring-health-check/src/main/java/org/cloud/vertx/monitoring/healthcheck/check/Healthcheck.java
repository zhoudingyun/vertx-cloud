package org.cloud.vertx.monitoring.healthcheck.check;

import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.redis.client.Redis;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.sqlclient.Pool;

public interface Healthcheck {
    default void check(Pool pool, String registerName) {
    }

    default void check(JDBCClient jdbcClient, String registerName) {
    }

    default void check(Redis redis, String registerName) {
    }

    default void check(KafkaProducer<?, ?> producer, String registerName) {
    }

    default void check(ServiceDiscovery discovery, String registerName) {
    }

    default void check(ServiceDiscovery discovery) {
    }

    default void check(Vertx vertx) {
    }

    default String getRegisterName(String registerName) {
        return registerName;
    }
}
