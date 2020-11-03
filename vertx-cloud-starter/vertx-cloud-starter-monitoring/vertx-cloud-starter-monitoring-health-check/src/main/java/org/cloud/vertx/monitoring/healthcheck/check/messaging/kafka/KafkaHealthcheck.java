package org.cloud.vertx.monitoring.healthcheck.check.messaging.kafka;

import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * messaging kafka健康检测
 *
 * @author frank
 */
public class KafkaHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "messaging.kafka";

    public KafkaHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(KafkaProducer<?, ?> producer, String registerName) {
        healthCheckHandler.register(this.getRegisterName(registerName), promise -> {
            AtomicBoolean exception = new AtomicBoolean(false);
            producer.exceptionHandler(t -> {
                exception.set(true);
                promise.fail(t);
            });
            if (!exception.get()) {
                promise.complete(Status.OK());
            }
        });
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
