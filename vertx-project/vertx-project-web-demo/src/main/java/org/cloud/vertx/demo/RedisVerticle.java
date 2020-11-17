package org.cloud.vertx.demo;

import io.netty.util.internal.StringUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import org.cloud.vertx.core.util.VertxBeanUtils;
import io.vertx.core.AbstractVerticle;

public class RedisVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisVerticle.class);

    private String nodeName;

    public RedisVerticle() {
    }

    public RedisVerticle(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public void start() throws Exception {
        JsonObject redisConfig = new JsonObject();
        redisConfig.put("connectionString", "redis://localhost:6379/0");
        redisConfig.put("poolCleanerInterval", 5000);
        redisConfig.put("poolRecycleTimeout", 3000);
        redisConfig.put("maxPoolSize", 20);
        Redis redis = Redis.createClient(vertx, new RedisOptions(redisConfig));
        if (StringUtil.isNullOrEmpty(nodeName)) {
            VertxBeanUtils.put(Redis.class, redis);
        } else {
            VertxBeanUtils.put(nodeName, redis);
        }
    }

}
