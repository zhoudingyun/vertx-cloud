package org.cloud.vertx.monitoring.healthcheck.checkpoint.impl;

import org.cloud.vertx.monitoring.healthcheck.checkpoint.EventBusServiceEndpoint;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class EventBusServiceEndpointImpl implements EventBusServiceEndpoint {
    @Override
    public EventBusServiceEndpoint endpoint(Handler<AsyncResult<Void>> handler) {
        handler.handle(Future.succeededFuture());
        return this;
    }
}
