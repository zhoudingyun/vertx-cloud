package org.cloud.verx.starter.health.check;

import io.vertx.servicediscovery.ServiceDiscovery;

public class HttpEndpointHealthCheck {
    private ServiceDiscovery serviceDiscovery;
    private String serviceName;

    public HttpEndpointHealthCheck(ServiceDiscovery serviceDiscovery, String serviceName) {
        this.serviceDiscovery = serviceDiscovery;
        this.serviceName = serviceName;
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
