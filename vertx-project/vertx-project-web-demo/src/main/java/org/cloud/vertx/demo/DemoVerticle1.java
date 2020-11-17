package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.redis.client.Redis;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.monitoring.healthcheck.HealthCheckBuilder;
import org.cloud.vertx.monitoring.healthcheck.HealthCheckVerticle;


public class DemoVerticle1 extends AbstractVerticle {
    public void start() throws Exception {
        vertx.setPeriodic(10,a->{
            vertx.runOnContext(al -> {
                try {
                    Thread.sleep(5000);
                    System.out.println("d1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
