package examples;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.cloud.vertx.data.redis.RedisVerticle;

public class Examples extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
                .addStore(new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "/home/frank/workspace/myproject/vertx-cloud/vertx-cloud-starter/vertx-cloud-starter-data/vertx-cloud-starter-data-redis/src/main/resources/conf/config.json"))));
        retriever.getConfig().onSuccess(config->{
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setConfig(config);

            vertx.deployVerticle(new RedisVerticle(), deploymentOptions).onSuccess(s -> {
                System.out.println("ok");
            }).onFailure(e -> {
                System.out.println(e.getCause());
            });
        }).onFailure(e->{

        });

        vertx.deployVerticle(new RedisOperation());

    }
}
