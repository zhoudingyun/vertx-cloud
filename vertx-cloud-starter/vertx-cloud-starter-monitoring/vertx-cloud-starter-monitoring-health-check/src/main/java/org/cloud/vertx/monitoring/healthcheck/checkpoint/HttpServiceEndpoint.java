package org.cloud.vertx.monitoring.healthcheck.checkpoint;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpServiceEndpoint {
    public static final String PATH = "/service/discovery/health/check/httpendpoint";

    public HttpServiceEndpoint(Router router) {
        router.get(PATH).handler(this::endpoint);
    }

    private void endpoint(RoutingContext routingContext) {
        routingContext.response().end();
    }
}
