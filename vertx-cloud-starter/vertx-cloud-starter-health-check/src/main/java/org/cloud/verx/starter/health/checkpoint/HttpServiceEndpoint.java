package org.cloud.verx.starter.health.checkpoint;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpServiceEndpoint {
    public static final String PATH = "/health/check/service/httpendpoint";

    public HttpServiceEndpoint(Router router) {
        router.get(PATH).handler(this::endpoint);
    }

    private void endpoint(RoutingContext routingContext) {
        routingContext.response().end();
    }
}
