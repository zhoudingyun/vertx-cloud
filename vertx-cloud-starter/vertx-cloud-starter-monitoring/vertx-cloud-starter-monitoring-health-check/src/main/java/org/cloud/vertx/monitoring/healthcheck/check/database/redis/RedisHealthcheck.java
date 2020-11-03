package org.cloud.vertx.monitoring.healthcheck.check.database.redis;

import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.redis.client.Redis;
import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

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
                promise -> redis.connect(connection -> {
                    if (connection.failed()) {
                        promise.fail(connection.cause());
                    } else {
                        promise.complete(Status.OK());
                    }
                }));
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
