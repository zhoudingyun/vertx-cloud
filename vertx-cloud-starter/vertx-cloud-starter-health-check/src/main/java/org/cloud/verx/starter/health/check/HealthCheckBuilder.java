package org.cloud.verx.starter.health.check;

import org.cloud.verx.starter.health.check.cluster.hazelcast.HazelcastClusterManagerHealthcheck;
import org.cloud.verx.starter.health.check.cluster.ignite.IgniteClusterManagerHealthcheck;
import org.cloud.verx.starter.health.check.database.mysql.client.MysqlClientHealthcheck;
import org.cloud.verx.starter.health.check.database.mysql.jdbc.MysqlJdbcHealthcheck;
import org.cloud.verx.starter.health.check.database.redis.RedisHealthcheck;
import org.cloud.verx.starter.health.check.eventbus.EventBusHealthcheck;
import org.cloud.verx.starter.health.check.messaging.kafka.KafkaHealthcheck;
import org.cloud.verx.starter.health.check.service.eventbusservice.ServiceDiscoveryEventbusServiceHealthcheck;
import org.cloud.verx.starter.health.check.service.httpendpoint.ServiceDiscoveryHttpEndPointHealthcheck;
import org.cloud.verx.starter.health.checkpoint.EventBusEndpoint;
import org.cloud.verx.starter.health.checkpoint.EventBusServiceEndpoint;
import org.cloud.verx.starter.health.checkpoint.HttpServiceEndpoint;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.redis.client.Redis;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import io.vertx.sqlclient.Pool;

import java.util.ArrayList;
import java.util.List;

public class HealthCheckBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckBuilder.class);
    private List<Pool> poolList = new ArrayList<>();
    private List<JDBCClient> jdbcClientList = new ArrayList<>();
    private List<Redis> redisList = new ArrayList<>();
    private List<KafkaProducer<?, ?>> kafkaProducerList = new ArrayList<>();
    private Vertx vertx;
    private Router router;
    private HealthCheckHandler healthCheckHandler;
    private HealthChecks healthChecks;

    private boolean openClusterHealthCheck;
    private boolean openEventBusHealthCheck;
    private EventBusEndpoint eventBusEndpoint;
    private ServiceDiscovery eventBusServiceServiceDiscovery;
    private ServiceDiscovery httpEndpointDiscoveryServiceDiscovery;

    public HealthCheckBuilder(Vertx vertx, Router router) {
        this.vertx = vertx;
        this.router = router;
        eventBusEndpoint = new EventBusEndpoint(vertx);
    }

    public Router getRouter() {
        return router;
    }

    public HealthCheckHandler getHealthCheckHandler() {
        return healthCheckHandler;
    }

    public HealthCheckBuilder setHealthCheckHandler(HealthCheckHandler healthCheckHandler) {
        this.healthCheckHandler = healthCheckHandler;
        return this;
    }

    public HealthChecks getHealthChecks() {
        return healthChecks;
    }

    public HealthCheckBuilder setHealthChecks(HealthChecks healthChecks) {
        this.healthChecks = healthChecks;
        return this;
    }

    public HealthCheckBuilder addPoolHealthCheck(Pool pool) {
        poolList.add(pool);
        return this;
    }

    public HealthCheckBuilder addJDBCClientHealthCheck(JDBCClient jdbcClient) {
        jdbcClientList.add(jdbcClient);
        return this;
    }

    public HealthCheckBuilder addRedisHealthCheck(Redis redis) {
        redisList.add(redis);
        return this;
    }

    public HealthCheckBuilder addKafkaProducerHealthCheck(KafkaProducer<?, ?> producer) {
        kafkaProducerList.add(producer);
        return this;
    }

    public HealthCheckBuilder addEventBusServiceHealthCheck(ServiceDiscovery serviceDiscovery) {
        this.eventBusServiceServiceDiscovery = serviceDiscovery;
        return this;
    }

    public HealthCheckBuilder addHttpEndpointHealthCheck(ServiceDiscovery serviceDiscovery) {
        this.httpEndpointDiscoveryServiceDiscovery = serviceDiscovery;
        return this;
    }

    public HealthCheckBuilder openClusterHealthCheck() {
        openClusterHealthCheck = true;
        return this;
    }

    public HealthCheckBuilder openEventBusHealthCheck() {
        openEventBusHealthCheck = true;
        return this;
    }

    /**
     * 创建EventBus健康检测点
     *
     * @return HealthCheckBuilder
     */
    public HealthCheckBuilder createEventBusCheckpoint() {
        if (eventBusEndpoint != null) {
            eventBusEndpoint.createHealthCheckEndpoint();
            return this;
        } else {
            LOGGER.error(new Exception("Vertx is not set or address is blank"));
            return null;
        }
    }

    /**
     * 创建EventBusService健康检测点
     *
     * @param discovery
     * @return HealthCheckBuilder
     */
    public HealthCheckBuilder createEventBusServiceCheckpoint(ServiceDiscovery discovery) {
        EventBusServiceEndpoint.createProxyAndPublish(vertx, discovery);
        return this;
    }

    /**
     * 创建HttpEndpoint健康检测点
     *
     * @return
     */
    public HealthCheckBuilder createHttpEndpointCheckpoint() {
        if (router != null) {
            HttpServiceEndpoint httpServiceEndpoint = new HttpServiceEndpoint(router);
            return this;
        } else {
            LOGGER.error(new Exception("router is null"));
            return null;
        }
    }

    public void build() {
        if (healthCheckHandler != null) {
            // MysqlClient
            buildMysqlClientHealthcheck(healthCheckHandler, poolList);
            // MysqlJdbc
            buildMysqlJdbcHealthcheck(healthCheckHandler, jdbcClientList);
            // Redis
            buildRedisHealthcheck(healthCheckHandler, redisList);
            // Kafka
            buildKafkaHealthcheck(healthCheckHandler, kafkaProducerList);
            if (vertx != null) {
                // Cluster
                if (openClusterHealthCheck) {
                    buildClusterHealthcheck(healthCheckHandler, vertx);
                }
                if (openEventBusHealthCheck) {
                    // EventBus
                    buildEventBusHealthcheck(healthCheckHandler, vertx);
                }
                if (eventBusServiceServiceDiscovery != null) {
                    // ServiceDiscoveryEventbusService
                    buildServiceDiscoveryEventbusServiceHealthcheck(healthCheckHandler, eventBusServiceServiceDiscovery);
                } else {
                    LOGGER.warn("eventBusServiceServiceDiscovery is empty, dependent health detection creation failed");
                }

                if (httpEndpointDiscoveryServiceDiscovery != null) {
                    // ServiceDiscoveryHttpEndPoint
                    buildServiceDiscoveryHttpEndPointHealthcheck(healthCheckHandler, httpEndpointDiscoveryServiceDiscovery);
                } else {
                    LOGGER.warn("httpEndpointDiscoveryServiceDiscovery is empty, dependent health detection creation failed");
                }
            } else {
                LOGGER.warn("Vertx is empty, dependent health detection creation failed");
            }
        } else {
            LOGGER.error(new RuntimeException("Failed to create health check because healthcheckhandler is null"));
        }
    }

    private void buildMysqlClientHealthcheck(HealthCheckHandler healthCheckHandler, List<Pool> list) {
        Healthcheck healthcheck = new MysqlClientHealthcheck(healthCheckHandler);
        for (int i = 0; i < list.size(); i++) {
            healthcheck.check(list.get(i), i + "");
        }
    }

    private void buildMysqlJdbcHealthcheck(HealthCheckHandler healthCheckHandler, List<JDBCClient> list) {
        Healthcheck healthcheck = new MysqlJdbcHealthcheck(healthCheckHandler);
        for (int i = 0; i < list.size(); i++) {
            healthcheck.check(list.get(i), i + "");
        }
    }

    private void buildRedisHealthcheck(HealthCheckHandler healthCheckHandler, List<Redis> list) {
        Healthcheck healthcheck = new RedisHealthcheck(healthCheckHandler);
        for (int i = 0; i < list.size(); i++) {
            healthcheck.check(list.get(i), i + "");
        }
    }

    private void buildKafkaHealthcheck(HealthCheckHandler healthCheckHandler, List<KafkaProducer<?, ?>> list) {
        Healthcheck healthcheck = new KafkaHealthcheck(healthCheckHandler);
        for (int i = 0; i < list.size(); i++) {
            healthcheck.check(list.get(i), i + "");
        }
    }

    private void buildEventBusHealthcheck(HealthCheckHandler healthCheckHandler, Vertx vertx) {
        Healthcheck eventBusHealthcheck = new EventBusHealthcheck(healthCheckHandler);
        eventBusHealthcheck.check(vertx);
    }

    private void buildServiceDiscoveryEventbusServiceHealthcheck(HealthCheckHandler healthCheckHandler, ServiceDiscovery serviceDiscovery) {
        Healthcheck serviceDiscoveryEventbusServiceHealthcheck = new ServiceDiscoveryEventbusServiceHealthcheck(healthCheckHandler);
        serviceDiscoveryEventbusServiceHealthcheck.check(serviceDiscovery, "EventbusService");
    }

    private void buildServiceDiscoveryHttpEndPointHealthcheck(HealthCheckHandler healthCheckHandler, ServiceDiscovery serviceDiscovery) {
        Healthcheck serviceDiscoveryHttpEndPointHealthcheck = new ServiceDiscoveryHttpEndPointHealthcheck(healthCheckHandler);
        serviceDiscoveryHttpEndPointHealthcheck.check(serviceDiscovery, "HttpEndPoint");
    }

    private void buildClusterHealthcheck(HealthCheckHandler healthCheckHandler, Vertx vertx) {
        VertxInternal vertxInternal = (VertxInternal) vertx;
        ClusterManager clusterManager = vertxInternal.getClusterManager();
        Healthcheck healthcheck = null;
        if (clusterManager instanceof HazelcastClusterManager) {
            healthcheck = new HazelcastClusterManagerHealthcheck(healthCheckHandler);
        } else if (clusterManager instanceof IgniteClusterManager) {
            healthcheck = new IgniteClusterManagerHealthcheck(healthCheckHandler);
        }
        if (healthcheck == null) {
            LOGGER.error(new RuntimeException("The cluster health check is not supported"));
        }
        healthcheck.check(vertx);
    }
}
