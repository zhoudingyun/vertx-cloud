package org.cloud.vertx.monitoring.healthcheck.check.eventbus;

import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import org.cloud.vertx.monitoring.healthcheck.checkpoint.EventBusEndpoint;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

/**
 * EventBus健康检测
 *
 * @author frank
 */
public class EventBusHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "eventbus";

    public EventBusHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(Vertx vertx) {
        healthCheckHandler.register(this.getRegisterName(EventBusEndpoint.ADDRESS),
                promise -> vertx.eventBus().request(EventBusEndpoint.ADDRESS, 1, reply -> {
                    if (reply.succeeded()) {
                        promise.complete(Status.OK());
                    } else {
                        promise.fail(reply.cause());
                    }
                }));
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
