package org.cloud.verx.starter.health.check.cluster.ignite;

import org.cloud.verx.starter.health.check.Healthcheck;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import org.cloud.verx.starter.health.check.SubHealthcheck;

/**
 * mysql client健康检测
 *
 * @author frank
 */
public class IgniteClusterManagerHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "cluster.ignite";

    public IgniteClusterManagerHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(Vertx vertx) {
        healthCheckHandler.register(this.getRegisterName(PRE_REGISTER_NAME),
                promise -> {
                    promise.complete(Status.OK());
                }
        );
    }
}
