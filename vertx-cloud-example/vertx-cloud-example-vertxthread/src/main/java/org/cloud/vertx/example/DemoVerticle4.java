package org.cloud.vertx.example;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
public class DemoVerticle4 extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route("/").handler(r -> {
            r.response().end("ok");
        });
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}
