package org.cloud.vertx.example;

import io.vertx.core.AbstractVerticle;

public class DemoVerticle1 extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(new DemoVerticle2());
    }
}
