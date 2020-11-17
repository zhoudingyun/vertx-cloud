package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;


public class DemoVerticle3 extends AbstractVerticle {
    public void start() throws Exception {
        vertx.setPeriodic(5, a->{
            vertx.eventBus().send("/test/wait", "11");
        });
    }
}
