package org.cloud.verx.starter.health.checkpoint;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;
import org.cloud.verx.starter.health.checkpoint.impl.EventBusServiceEndpointImpl;

@ProxyGen
@VertxGen
public interface EventBusServiceEndpoint {
    String ADDRESS = "/health/check/service/eventbusendpoint";

    static EventBusServiceEndpoint createProxyAndPublish(Vertx vertx, ServiceDiscovery discovery) {
        EventBusServiceEndpoint eventBusServiceEndpoint = new EventBusServiceEndpointImpl();
        new ServiceBinder(vertx).setAddress(ADDRESS).register(EventBusServiceEndpoint.class, eventBusServiceEndpoint);

        Record record = EventBusService.createRecord(EventBusServiceEndpoint.class.getName(),
                EventBusServiceEndpoint.ADDRESS, EventBusServiceEndpoint.class);
        discovery.publish(record, ar -> {
            if (ar.failed()) {
                throw new RuntimeException(record.getName() + "fail published!", ar.cause());
            }
        });

        return eventBusServiceEndpoint;
    }

    @Fluent
    EventBusServiceEndpoint endpoint(Handler<AsyncResult<Void>> handler);
}
