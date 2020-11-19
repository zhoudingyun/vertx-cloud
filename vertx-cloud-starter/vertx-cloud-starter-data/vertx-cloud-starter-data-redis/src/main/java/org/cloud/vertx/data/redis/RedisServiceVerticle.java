package org.cloud.vertx.data.redis;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.RedisDataSource;
import org.cloud.vertx.core.verticle.VertxCloudDataVerticle;


public class RedisServiceVerticle extends VertxCloudDataVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceVerticle.class);
    private static final String REDIS_CONFIG = DATA_CONFIG + "." + "redis";

    private String nodeName;

    public RedisServiceVerticle() {
    }

    public RedisServiceVerticle(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public void start() throws Exception {
        JsonObject redisConfig = verifyComponentConfig();
        Record record = null;
        if (StringUtil.isNullOrEmpty(nodeName)) {
            record = RedisDataSource.createRecord(
                    Redis.class.getName(),
                    redisConfig,
                    redisConfig
            );
        } else {
            record = RedisDataSource.createRecord(
                    nodeName,
                    redisConfig,
                    redisConfig
            );
        }

        // publish redis service
        this.getServiceDiscovery().publish(record)
                .onSuccess(resp -> {
                    LOGGER.info("publish redis success:" + redisConfig.toString());
                }).onFailure(throwable -> {
            LOGGER.error("publish redis fail:" + redisConfig.toString(), throwable);
        });
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject redisConfig = checkConfig(REDIS_CONFIG);

        if (StringUtil.isNullOrEmpty(nodeName)) {
            return redisConfig;
        }

        JsonObject nodeConfig = checkConfig(REDIS_CONFIG + "." + nodeName);

        JsonObject communalConfig = redisConfig.copy();
        communalConfig.remove(nodeName);
        return nodeConfig.mergeIn(communalConfig);
    }
}
