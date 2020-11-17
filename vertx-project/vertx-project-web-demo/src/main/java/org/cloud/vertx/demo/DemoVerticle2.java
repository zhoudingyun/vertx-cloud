package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;


public class DemoVerticle2 extends AbstractVerticle {
    public void start() throws Exception {
        vertx.eventBus().consumer("/test/wait").handler(msg -> {
            vertx.runOnContext(al -> {
                try {

                    int rs = 0;
                    while (rs < 1000000) {
                        int i = 1 + 2;
                        System.out.println(Thread.currentThread().getName() + ": "+ rs++);
                        rs++;
                    }

                    System.out.println("test/wait");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
