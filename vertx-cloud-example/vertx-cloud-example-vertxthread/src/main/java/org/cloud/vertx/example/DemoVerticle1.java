package org.cloud.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

public class DemoVerticle1 extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(new DemoVerticle2(), new DeploymentOptions().setWorker(true));

    }
}
