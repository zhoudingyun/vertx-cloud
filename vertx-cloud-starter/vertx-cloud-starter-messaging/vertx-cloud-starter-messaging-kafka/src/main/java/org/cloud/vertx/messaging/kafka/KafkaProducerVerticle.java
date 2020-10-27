package org.cloud.vertx.messaging.kafka;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.core.verticle.VertxCloudMessagingVerticle;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerVerticle<K, V> extends VertxCloudMessagingVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerVerticle.class);
    private String nodeName;

    public KafkaProducerVerticle() {
    }

    public KafkaProducerVerticle(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public void start() throws Exception {
        JsonObject nodeConfig = verifyComponentConfig();
        Map<String, String> map = new HashMap<>();
        nodeConfig.forEach(a -> {
            map.put(a.getKey(), a.getValue().toString());
        });
        KafkaProducer<K, V> producer = KafkaProducer.create(vertx, map);
        if (StringUtil.isNullOrEmpty(nodeName)) {
            VertxBeanUtils.put(KafkaProducer.class.getName(), producer);
        } else {
            VertxBeanUtils.put(nodeName, producer);
        }
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject messagingConfig = verifyCloudMessagingConfig();
        JsonObject kafkaConfig = messagingConfig.getJsonObject("kafka", null);
        if (kafkaConfig == null) {
            LOGGER.error(new RuntimeException("the property kafka is not configured, please check config.json."));
            System.exit(0);
        }

        JsonObject producerConfig = kafkaConfig.getJsonObject("producer", null);
        if (producerConfig == null) {
            LOGGER.error(new RuntimeException("the property producer is not configured, please check config.json."));
            System.exit(0);
        }

        if (StringUtil.isNullOrEmpty(nodeName)) {
            JsonObject communalConfig = kafkaConfig.copy();
            communalConfig.remove("producer");
            return producerConfig.mergeIn(communalConfig);
        }

        JsonObject nodeConfig = producerConfig.getJsonObject(nodeName, null);
        if (nodeConfig == null) {
            LOGGER.error(new RuntimeException("the property " + nodeName + " is not configured, please check config.json."));
            System.exit(0);
        }

        JsonObject communalProducerConfig = producerConfig.copy();
        communalProducerConfig.remove(nodeName);
        JsonObject communalKafkaConfig = kafkaConfig.copy();
        communalKafkaConfig.remove("producer");
        return nodeConfig.mergeIn(communalProducerConfig).mergeIn(communalKafkaConfig);
    }
}
