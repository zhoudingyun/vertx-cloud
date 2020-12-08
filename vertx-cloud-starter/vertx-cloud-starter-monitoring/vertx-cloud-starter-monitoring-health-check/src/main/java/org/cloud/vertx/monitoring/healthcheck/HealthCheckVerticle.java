package org.cloud.vertx.monitoring.healthcheck;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.core.verticle.VertxCloudMonitoringVerticle;
import org.cloud.vertx.monitoring.healthcheck.notice.NoticeService;
import org.cloud.vertx.monitoring.healthcheck.notice.impl.EmailNoticeServiceImpl;
import org.cloud.vertx.monitoring.healthcheck.notice.impl.MqNoticeServiceImpl;
import org.cloud.vertx.monitoring.healthcheck.util.IpUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * HealthCheckVerticle
 *
 * @author frank
 */
public class HealthCheckVerticle extends VertxCloudMonitoringVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckVerticle.class);

    private HealthCheckBuilder builder;

    /**
     * construction.
     *
     * @param builder
     */
    public HealthCheckVerticle(HealthCheckBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void start() throws Exception {
        if (builder == null) {
            LOGGER.error(new NullPointerException("the builder is null"));
            System.exit(1);
        }
        HealthChecks healthChecks = HealthChecks.create(vertx);
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.createWithHealthChecks(healthChecks);
        builder.setHealthChecks(healthChecks).setHealthCheckHandler(healthCheckHandler);

        // get Router
        Router router = builder.getRouter();
        // Create a health check route
        router.get("/health").handler(healthCheckHandler);
        builder.build();

        // Send unhealthy information
        JsonObject monitoringConfig = verifyComponentConfig();
        JsonObject healthCheckConfig = monitoringConfig.getJsonObject("health_check");
        // check health check config
        if (healthCheckConfig == null) {
            LOGGER.error(new RuntimeException("the property health_check is not configured, please check config.json."));
            System.exit(1);
        }
        JsonObject applicationConfig = null; // TODO
        vertx.eventBus().consumer("vertx.cloud.health", message -> healthChecks.invoke(message::reply));

        // The default detection cycle is 10000ms
        long periodic = healthCheckConfig.getLong("periodic", 10000l);
        if (healthCheckConfig.containsKey("messaging")) {
            // send by mq
            JsonObject messagingConfig = healthCheckConfig.getJsonObject("messaging");
            KafkaProducer<String, String> producer = VertxBeanUtils.get(KafkaProducer.class);
            if (producer == null) {
                producer = VertxBeanUtils.get(messagingConfig.getString("producer"));
            }

            if (producer == null) {
                LOGGER.error(new RuntimeException("Please configure the vertx.cloud.monitoring.health_check.messaging.producer in config.json"));
            }
            String topic = messagingConfig.getString("topic");
            if (StringUtil.isNullOrEmpty(topic)) {
                LOGGER.error(new RuntimeException("Please configure the vertx.cloud.monitoring.health_check.messaging.topic in config.json"));
            }
            NoticeService noticeService = new MqNoticeServiceImpl(topic, producer);
            vertx.setPeriodic(periodic, aLong -> {
                vertx.eventBus().<JsonObject>request("vertx.cloud.health", "", reply -> {
                    JsonObject jsonObject = reply.result().body();
                    if ("DOWN".equals(jsonObject.getString("status"))) {
                        String ip = IpUtils.getLocalHost();
                        jsonObject.put("ip", ip);
                        jsonObject.put("vertx.application.name", applicationConfig.getString("name"));
                        noticeService.notice(jsonObject);
                    }
                });
            });
        } else if (healthCheckConfig.containsKey("email")) {
            // send by email
            JsonObject emailConfig = healthCheckConfig.getJsonObject("email");
            NoticeService noticeService = new EmailNoticeServiceImpl(vertx, emailConfig);

            vertx.setPeriodic(periodic, aLong -> {
                vertx.eventBus().<JsonObject>request("vertx.cloud.health", "", reply -> {
                    JsonObject jsonObject = reply.result().body();
                    if ("DOWN".equals(jsonObject.getString("status"))) {
                        String ip = IpUtils.getLocalHost();
                        emailConfig.put("ip", ip);
                        emailConfig.put("vertx.application.name", applicationConfig.getString("name"));
                        noticeService.notice(emailConfig.put("html", jsonObject.toString()));
                    }
                });
            });
        }

        if (healthCheckConfig.containsKey("messaging")) {
            JsonObject messagingConfig = healthCheckConfig.getJsonObject("messaging");
            KafkaConsumer<String, String> consumer = VertxBeanUtils.get(messagingConfig.getString("consumer"));
            String topic = messagingConfig.getString("topic");
            if (consumer == null) {
                LOGGER.warn("The configuration file is missing a consumer, please configure the vertx.cloud.monitoring.health_check.messaging.consumer in config.json if you need to notify.");
            } else {
                if (StringUtil.isNullOrEmpty(topic)) {
                    LOGGER.error(new RuntimeException("Please configure the vertx.cloud.monitoring.health_check.messaging.topic in config.json"));
                }
            }

            if (healthCheckConfig.containsKey("email") && consumer != null) {
                Set<String> set = new HashSet<>();
                set.add(topic);
                consumer.subscribe(set);

                JsonObject emailConfig = healthCheckConfig.getJsonObject("email");
                if (emailConfig != null) {
                    NoticeService noticeService = new EmailNoticeServiceImpl(vertx, emailConfig);
                    consumer.handler(record -> {
                        try {
                            JsonObject jsonObject = new JsonObject(record.value());
                            emailConfig.put("ip", jsonObject.getString("ip"));
                            emailConfig.put("vertx.application.name", jsonObject.getString("vertx.application.name"));
                            jsonObject.remove("ip");
                            jsonObject.remove("vertx.application.name");
                            noticeService.notice(emailConfig.put("html", jsonObject.toString()));
                        } catch (Exception e) {
                            LOGGER.error(record.value(), e);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        return null;
    }
}
