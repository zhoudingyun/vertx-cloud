package org.cloud.vertx.example;

import io.vertx.core.AbstractVerticle;

public class DemoVerticle2 extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(10, aLong -> {
            vertx.runOnContext(aVoid -> {
                System.out.println("DemoVerticle2:" + Thread.currentThread().getName());
            });
        });
    }
}
