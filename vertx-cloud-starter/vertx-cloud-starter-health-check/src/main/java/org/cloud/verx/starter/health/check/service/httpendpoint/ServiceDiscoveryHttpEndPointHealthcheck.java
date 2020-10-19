package org.cloud.verx.starter.health.check.service.httpendpoint;

import org.cloud.verx.starter.health.checkpoint.HttpServiceEndpoint;
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
import org.cloud.verx.starter.health.check.Healthcheck;
import org.cloud.verx.starter.health.check.SubHealthcheck;

public class ServiceDiscoveryHttpEndPointHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "service.discovery";

    public ServiceDiscoveryHttpEndPointHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(ServiceDiscovery discovery, String serviceName, String registerName) {
        JsonObject filter = new JsonObject().put("name", serviceName);
        discovery.getRecords(new JsonObject()).onSuccess(records->{
           System.out.println(records.size());
        });
        healthCheckHandler.register(this.getRegisterName(registerName),
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

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
