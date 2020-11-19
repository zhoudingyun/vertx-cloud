package examples;

import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.RedisDataSource;
import org.cloud.vertx.core.verticle.VertxCloudDataVerticle;

import java.util.Arrays;


public class RedisOperation extends VertxCloudDataVerticle {

    public void start() throws Exception {
        ServiceDiscovery discovery = this.getServiceDiscovery();
        JsonObject filter = new JsonObject().put("name", Redis.class.getName());
        vertx.setTimer(3000, al->{
            discovery.getRecord(filter).onSuccess(record -> {
                ServiceReference reference = discovery.getReference(record);
                // Retrieve the service instance
                Redis client = reference.getAs(Redis.class);
                RedisAPI.api(client).set(Arrays.asList("asfdq3-3ewer/wer36sx/werwe", "asfdq3-3ewer/wer36sx/werwe"));
            });

            RedisDataSource.getRedisClient(this.getServiceDiscovery(), filter, ar -> {
                if (ar.succeeded()) {
                    Redis client = ar.result();
                    RedisAPI.api(client).set(Arrays.asList("wer1:wer1:asfdq3-3ewer/wer36sx/werwe", "asfdq3-3ewer/wer36sx/werwe"));
                }
            });
        });
    }

    @Override
    protected JsonObject verifyComponentConfig() {
        return null;
    }
}
