package org.cloud.verx.starter.health.checkpoint;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class EventBusEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusEndpoint.class);
    private Vertx vertx;
    public static final String ADDRESS = "address.vertxhealthcheckeventbus";

    public EventBusEndpoint(Vertx vertx) {
        this.vertx = vertx;
    }

    public void createHealthCheckEndpoint() {
        vertx.eventBus().consumer(ADDRESS).handler(message -> {
            message.reply(1);
        }).exceptionHandler(th -> {
            LOGGER.error(ADDRESS, th);
        });
    }
}
