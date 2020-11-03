package org.cloud.vertx.monitoring.healthcheck.check;

import io.vertx.ext.healthchecks.HealthCheckHandler;

public abstract class SubHealthcheck {
    protected HealthCheckHandler healthCheckHandler;

    public SubHealthcheck(HealthCheckHandler healthCheckHandler) {
        this.healthCheckHandler = healthCheckHandler;
    }
}
