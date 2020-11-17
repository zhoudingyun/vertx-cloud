package org.cloud.vertx.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.monitoring.healthcheck.HealthCheckBuilder;
import org.cloud.vertx.monitoring.healthcheck.HealthCheckVerticle;

import java.util.Arrays;
import java.util.UUID;


public class DemoVerticle extends AbstractVerticle {
    public void start() throws Exception {
        vertx.deployVerticle(new RedisVerticle()).onSuccess(s->{

            JsonObject config = config();
            DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);
            deploymentOptions.setConfig(config);


            System.out.println(11);
            // 创建路由
            Router router = Router.router(vertx);
            Redis redis = VertxBeanUtils.get(Redis.class);

            HealthCheckBuilder builder = new HealthCheckBuilder(vertx, router)
                    .createEventBusCheckpoint()
                    .createHttpEndpointCheckpoint()
                    .createEventBusServiceCheckpoint()
                    .addRedisHealthCheck(redis)
                    .openEventBusHealthCheck()
                    .openEventbusServiceHealthCheck()
                    .openClusterHealthCheck();
            HealthCheckVerticle healthCheckVerticle = new HealthCheckVerticle(builder);
            vertx.deployVerticle(healthCheckVerticle, deploymentOptions);

            HttpServer server = vertx.createHttpServer();
            server.requestHandler(router).
                    listen(8080, res -> {
                        if (res.succeeded()) {
                            System.out.println(222);
                        } else {
                            System.exit(1);
                            try {
                                stop();
                            } catch (Exception e) {
                            }
                        }
                    });


            vertx.deployVerticle(new DemoVerticle2());
            vertx.deployVerticle(new DemoVerticle2());
            vertx.deployVerticle(new DemoVerticle2());
            vertx.deployVerticle(new DemoVerticle2());
            vertx.deployVerticle(new DemoVerticle2());


            vertx.deployVerticle(new DemoVerticle3());
            vertx.deployVerticle(new DemoVerticle3());
            vertx.deployVerticle(new DemoVerticle3());

        }).onFailure(th->{
            System.out.println(th.getMessage());
        });
    }
}
