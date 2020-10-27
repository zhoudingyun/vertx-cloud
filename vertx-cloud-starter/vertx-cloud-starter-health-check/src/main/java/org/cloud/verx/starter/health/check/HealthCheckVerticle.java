package org.cloud.vertx.starter.health.check;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.CheckResult;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import org.cloud.vertx.starter.health.notice.EmailNoticeServiceImpl;
import org.cloud.vertx.starter.health.notice.NoticeService;

import java.util.List;

public class HealthCheckVerticle extends AbstractVerticle {
    private HealthCheckBuilder builder;

    public HealthCheckVerticle(HealthCheckBuilder builder) {
        this.builder = builder;
    }

    public void start() throws Exception {
        HealthChecks healthChecks = HealthChecks.create(vertx);
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.createWithHealthChecks(healthChecks);

        builder.setHealthChecks(healthChecks).setHealthCheckHandler(healthCheckHandler);

        Router router = builder.getRouter();
        router.get("/health").handler(healthCheckHandler);
        builder.build();

        JsonObject config = config();
        JsonObject vertxcloud = config.getJsonObject("vertx");
        JsonObject cloud = vertxcloud.getJsonObject("cloud");
        JsonObject email = cloud.getJsonObject("email");
        NoticeService noticeService = new EmailNoticeServiceImpl(vertx, email);
        vertx.setPeriodic(30000, a -> {
            vertx.eventBus().<JsonObject>request("vertx.cloud.health", "", reply -> {
                JsonObject jsonObject = reply.result().body();
                if ("DOWN".equals(jsonObject.getString("status"))) {
                    noticeService.notice(email.put("html", jsonObject.toString()));
                }
            });
        });

        vertx.eventBus().consumer("vertx.cloud.health", message -> healthChecks.invoke(message::reply));
    }
}
