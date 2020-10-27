package org.cloud.vertx.starter.health.check.service.eventbusservice;

import org.cloud.vertx.starter.health.check.Healthcheck;
import org.cloud.vertx.starter.health.checkpoint.EventBusServiceEndpoint;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import org.cloud.vertx.starter.health.check.SubHealthcheck;

public class ServiceDiscoveryEventbusServiceHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "service.discovery.eventbus-service";

    public ServiceDiscoveryEventbusServiceHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(ServiceDiscovery discovery, String registerName) {
        healthCheckHandler.register(this.getRegisterName(registerName), promise -> {
            EventBusService.getProxy(discovery, EventBusServiceEndpoint.class, ar -> {
                if (ar.succeeded()) {
                    EventBusServiceEndpoint service = ar.result();
                    service.endpoint(asyncResult -> {
                        if (asyncResult.succeeded()) {
                            promise.complete(Status.OK());
                        } else {
                            promise.fail(asyncResult.cause());
                        }
                    });
                } else {
                    promise.fail(ar.cause());
                }
            });
        });
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
