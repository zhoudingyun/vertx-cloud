package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;

public class DemoVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {

        String a = "robot:map:%s";
        String key = String.format(a, "11-04-f8-81/room_layer/1605232294383-1605232294383");
        System.out.println(key);



        /*vertx.deployVerticle(HttpServerVerticle.class, new DeploymentOptions());
        vertx.deployVerticle(RedisVerticle.class, new DeploymentOptions());
*/

    }

    @Override
    public void stop() throws Exception {
    }
}
