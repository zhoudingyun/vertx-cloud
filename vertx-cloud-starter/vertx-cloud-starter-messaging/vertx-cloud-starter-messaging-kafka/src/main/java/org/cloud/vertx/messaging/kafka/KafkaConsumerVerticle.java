package org.cloud.vertx.messaging.kafka;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.servicediscovery.Record;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.core.verticle.VertxCloudMessagingVerticle;

import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerVerticle<K, V> extends VertxCloudMessagingVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerVerticle.class);
    private static final String KAFKA_CONFIG = MESSAGING_CONFIG + "." + "kafka";
    private String nodeName;

    public <K, V> KafkaConsumerVerticle() {
    }

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

        Record record;
        if (StringUtil.isNullOrEmpty(nodeName)) {
            VertxBeanUtils.put(KafkaConsumer.class, consumer);
        } else {
            VertxBeanUtils.put(nodeName, consumer);
        }
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject kafkaConfig = super.checkConfig(KAFKA_CONFIG);

        JsonObject consumerConfig = kafkaConfig.getJsonObject("consumer", null);
        if (consumerConfig == null) {
            throw new RuntimeException("the property " + KAFKA_CONFIG + ".consumer is not configured, please check config.json.");
        }

        if (StringUtil.isNullOrEmpty(nodeName)) {
            JsonObject communalConfig = kafkaConfig.copy();
            communalConfig.remove("consumer");
            return consumerConfig.mergeIn(communalConfig);
        }

        JsonObject nodeConfig = consumerConfig.getJsonObject(nodeName, null);
        if (nodeConfig == null) {
            throw new RuntimeException("the property " + KAFKA_CONFIG + ".consumer." + nodeName + " is not configured, please check config.json.");
        }

        JsonObject communalConsumerConfig = consumerConfig.copy();
        communalConsumerConfig.remove(nodeName);
        JsonObject communalKafkaConfig = kafkaConfig.copy();
        communalKafkaConfig.remove("consumer");
        return nodeConfig.mergeIn(communalConsumerConfig).mergeIn(communalKafkaConfig);
    }
}
