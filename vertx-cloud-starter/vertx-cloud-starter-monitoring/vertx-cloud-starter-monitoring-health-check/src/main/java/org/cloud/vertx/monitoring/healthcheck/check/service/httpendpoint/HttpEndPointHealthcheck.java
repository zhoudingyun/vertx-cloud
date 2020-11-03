package org.cloud.vertx.monitoring.healthcheck.check.service.httpendpoint;

import io.vertx.servicediscovery.Record;
import org.cloud.vertx.monitoring.healthcheck.checkpoint.HttpServiceEndpoint;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

public class HttpEndPointHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "service.discovery.http-endpoint";

    public HttpEndPointHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(ServiceDiscovery discovery, String registerName) {
        discovery.getRecords(new JsonObject()).onSuccess(records -> {
            for (Record record : records) {
                if ("http-endpoint".equals(record.getType())) {
                    JsonObject filter = new JsonObject().put("name", record.getName());
                    healthCheckHandler.register(this.getRegisterName(record.getName()),
                            promise -> {
                                HttpEndpoint.getWebClient(discovery, filter, client -> {
                                    if (client.failed()) {
                                        promise.fail(client.cause());
                                    } else {
                                        WebClient webClient = client.result();
                                        HttpRequest<Buffer> request = webClient.get(HttpServiceEndpoint.PATH);
                                        Future<HttpResponse<Buffer>> response = request.send();
                                        response.onSuccess(a -> {
                                            promise.complete(Status.OK());
                                        }).onFailure(e -> {
                                            promise.fail(e);
                                        });
                                    }
                                });
                            }
                    );
                }
            }
        });
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
