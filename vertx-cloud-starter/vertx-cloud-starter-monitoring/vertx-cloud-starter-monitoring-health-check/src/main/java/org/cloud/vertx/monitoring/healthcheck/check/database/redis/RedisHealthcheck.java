package org.cloud.vertx.monitoring.healthcheck.check.database.redis;

import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

import java.util.Arrays;

/**
 * redis健康检测
 *
 * @author frank
 */
public class RedisHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "database.redis";

    public RedisHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(Redis redis, String registerName) {
        healthCheckHandler.register(this.getRegisterName(registerName),
                promise -> {
                    redis.connect().
                    RedisAPI.api(redis).set(Arrays.asList("vertx:cloud:monitoring:healthcheck", "1")).onSuccess(response -> {
                        promise.complete(Status.OK());
                    }).onFailure(throwable -> {
                        promise.fail(throwable);
                    });
                });
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
