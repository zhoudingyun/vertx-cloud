package org.cloud.vertx.data.redis;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.RedisDataSource;
import org.cloud.vertx.core.verticle.VertxCloudDataVerticle;

public class RedisVerticle extends VertxCloudDataVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisVerticle.class);

    private String nodeName;

    public RedisVerticle() {
    }

    public RedisVerticle(String nodeName) {
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

        this.getServiceDiscovery().publish(record);
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject dataConfig = verifyCloudDataConfig();

        JsonObject redisConfig = dataConfig.getJsonObject("redis", null);
        if (redisConfig == null) {
            LOGGER.error(new RuntimeException("the property redis is not configured, please check config.json."));
            System.exit(1);
        }

        if (StringUtil.isNullOrEmpty(nodeName)) {
            return redisConfig;
        }

        JsonObject nodeConfig = redisConfig.getJsonObject(nodeName, null);
        if (nodeConfig == null) {
            LOGGER.error(new RuntimeException("the property " + nodeConfig + " is not configured, please check config.json."));
            System.exit(1);
        }

        JsonObject communalConfig = redisConfig.copy();
        communalConfig.remove(nodeName);
        return nodeConfig.mergeIn(communalConfig);
    }
}
