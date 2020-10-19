package org.cloud.verx.starter.health.check;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;

public class HealthCheckVerticle extends AbstractVerticle {
    private HealthCheckBuilder builder;

    public HealthCheckVerticle(HealthCheckBuilder builder) {
        this.builder = builder;
    }

    public void start() throws Exception {
        HealthChecks healthChecks = HealthChecks.create(vertx);
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.createWithHealthChecks(healthChecks);

        builder.setHealthChecks(healthChecks).setHealthCheckHandler(healthCheckHandler);
        Router router = builder.getRouter();
        router.get("/health").handler(healthCheckHandler);
        builder.build();
    }
}
