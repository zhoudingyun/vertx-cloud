package org.cloud.vertx.example;

import io.vertx.core.AbstractVerticle;
public class DemoVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.deployVerticle(new DemoVerticle1());
        vertx.deployVerticle(new DemoVerticle3());
    }
}
