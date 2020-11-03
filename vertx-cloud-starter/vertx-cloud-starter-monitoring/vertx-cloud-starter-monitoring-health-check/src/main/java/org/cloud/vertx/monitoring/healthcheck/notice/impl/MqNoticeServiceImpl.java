package org.cloud.vertx.monitoring.healthcheck.notice.impl;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.cloud.vertx.monitoring.healthcheck.notice.NoticeService;

/**
 * 健康检测发送到消息队列
 *
 * @author frank
 */
public class MqNoticeServiceImpl implements NoticeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqNoticeServiceImpl.class);
    private String topic;
    private KafkaProducer<String, String> producer;

    public MqNoticeServiceImpl(String topic, KafkaProducer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void notice(JsonObject data) {
        KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(topic, data.toString());
        producer.write(record).onFailure(throwable -> {
            LOGGER.error(data.toString(), throwable);
        });
    }
}
