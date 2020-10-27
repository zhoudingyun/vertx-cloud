package org.cloud.vertx.data.redis;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import org.cloud.vertx.core.util.VertxBeanUtils;
import org.cloud.vertx.core.verticle.VertxCloudDataVerticle;

public class RedisVerticle extends VertxCloudDataVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisVerticle.class);

    private String nodeName;

    public RedisVerticle(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public void start() throws Exception {
        JsonObject redisConfig = verifyComponentConfig();
        Redis redis = Redis.createClient(vertx, new RedisOptions(redisConfig));
        VertxBeanUtils.put(nodeName, redis);
        redis.connect().onSuccess(ar -> {
            RedisAPI redisAPI = RedisAPI.api(ar);
            VertxBeanUtils.put(nodeName, redisAPI);
        }).onFailure(ex -> {
            LOGGER.error(ex);
        });
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        JsonObject dataConfig = verifyCloudDataConfig();

        JsonObject redisConfig = dataConfig.getJsonObject("redis", null);
        if (redisConfig == null) {
            LOGGER.error(new RuntimeException("the property redis is not configured, please check config.json."));
            System.exit(0);
        }

        JsonObject nodeConfig = redisConfig.getJsonObject(nodeName, null);
        if (nodeConfig == null) {
            LOGGER.error(new RuntimeException("the property " + nodeConfig + " is not configured, please check config.json."));
            System.exit(0);
        }

        return nodeConfig.mergeIn(redisConfig, true);
    }
}
