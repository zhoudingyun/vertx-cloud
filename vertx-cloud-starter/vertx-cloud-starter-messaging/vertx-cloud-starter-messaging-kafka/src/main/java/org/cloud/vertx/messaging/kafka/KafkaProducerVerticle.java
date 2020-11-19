package org.cloud.vertx.messaging.kafka;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.MessageSource;
import org.cloud.vertx.core.verticle.VertxCloudMessagingVerticle;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerVerticle<K, V> extends VertxCloudMessagingVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerVerticle.class);
    private static final String KAFKA_CONFIG = MESSAGING_CONFIG + "." + "kafka";
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

        Record record;
        if (StringUtil.isNullOrEmpty(nodeName)) {
            record = MessageSource.createRecord(
                    KafkaProducer.class.getName(), // The service name
                    "some-address" // The event bus address
            );
        } else {
            record = MessageSource.createRecord(
                    nodeName, // The service name
                    "some-address" // The event bus address
            );
        }

        this.getServiceDiscovery().publish(record).onSuccess(resp -> {
            LOGGER.info("publish Kafka Producer success:" + nodeConfig.toString());
        }).onFailure(throwable -> {
            LOGGER.error("publish Kafka Producer  fail:" + nodeConfig.toString(), throwable);
        });
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject kafkaConfig = super.checkConfig(KAFKA_CONFIG);

        JsonObject producerConfig = kafkaConfig.getJsonObject("producer", null);
        if (producerConfig == null) {
            throw new RuntimeException("the property " + KAFKA_CONFIG + ".producer is not configured, please check config.json.");
        }

        if (StringUtil.isNullOrEmpty(nodeName)) {
            JsonObject communalConfig = kafkaConfig.copy();
            communalConfig.remove("producer");
            return producerConfig.mergeIn(communalConfig);
        }

        JsonObject nodeConfig = producerConfig.getJsonObject(nodeName, null);
        if (nodeConfig == null) {
            throw new RuntimeException("the property " + KAFKA_CONFIG + ".producer." + nodeName + " is not configured, please check config.json.");
        }

        JsonObject communalProducerConfig = producerConfig.copy();
        communalProducerConfig.remove(nodeName);
        JsonObject communalKafkaConfig = kafkaConfig.copy();
        communalKafkaConfig.remove("producer");
        return nodeConfig.mergeIn(communalProducerConfig).mergeIn(communalKafkaConfig);
    }
}
