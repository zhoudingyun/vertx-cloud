package org.cloud.vertx.messaging.kafka;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.core.verticle.VertxCloudMessagingVerticle;

import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerVerticle<K, V> extends VertxCloudMessagingVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerVerticle.class);
    private String nodeName;

    public <K, V> KafkaConsumerVerticle(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public void start() throws Exception {
        JsonObject nodeConfig = verifyComponentConfig();
        Map<String, String> map = new HashMap<>();
        nodeConfig.forEach(a -> {
            map.put(a.getKey(), a.getValue().toString());
        });
        KafkaConsumer<K, V> consumer = KafkaConsumer.create(vertx, map);
        VertxBeanUtils.put(nodeName, consumer);
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject messagingConfig = verifyCloudMessagingConfig();
        JsonObject kafkaConfig = messagingConfig.getJsonObject("kafka", null);
        if (kafkaConfig == null) {
            LOGGER.error(new RuntimeException("the property kafka is not configured, please check config.json."));
            System.exit(0);
        }

        JsonObject consumerConfig = kafkaConfig.getJsonObject("consumer", null);
        if (consumerConfig == null) {
            LOGGER.error(new RuntimeException("the property consumer is not configured, please check config.json."));
            System.exit(0);
        }

        JsonObject nodeConfig = consumerConfig.getJsonObject(nodeName, null);
        if (nodeConfig == null) {
            LOGGER.error(new RuntimeException("the property " + nodeName + " is not configured, please check config.json."));
            System.exit(0);
        }

        return nodeConfig.mergeIn(consumerConfig, true).mergeIn(kafkaConfig, true);
    }
}
