package org.cloud.vertx.monitoring.healthcheck.check.cluster.hazelcast;

import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.spi.cluster.hazelcast.impl.HazelcastNodeInfo;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

import java.util.List;

/**
 * Hazelcast Manager健康检测
 *
 * @author frank
 */
public class HazelcastClusterManagerHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastClusterManagerHealthcheck.class);
    private static final String PRE_REGISTER_NAME = "cluster.hazelcast";

    public HazelcastClusterManagerHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(Vertx vertx) {
        healthCheckHandler.register(this.getRegisterName(PRE_REGISTER_NAME),
                promise -> {
                    VertxInternal vertxInternal = (VertxInternal) vertx;
                    ClusterManager clusterManager = vertxInternal.getClusterManager();
                    checkInvalidNode(vertx, clusterManager, promise);
                }
        );
    }

    private void checkInvalidNode(Vertx vertx, ClusterManager clusterManager, Promise promise) {
        List<String> list = clusterManager.getNodes();
        vertx.sharedData().<String, HazelcastNodeInfo>getAsyncMap("__vertx.nodeInfo").onSuccess(nodeInfos -> {
            nodeInfos.keys().onSuccess(nIds -> {
                if (clusterManager.getNodes().size() == nIds.size()) {
                    promise.complete(Status.OK());
                    LOGGER.info("the member total : " + clusterManager.getNodes().size() + " the __vertx.nodeInfo total: " + nIds.size());
                } else {
                    promise.fail("the member total : " + clusterManager.getNodes().size() + " the __vertx.nodeInfo total: " + nIds.size());
                    LOGGER.error("the member total : " + clusterManager.getNodes().size() + " the __vertx.nodeInfo total: " + nIds.size());
                }
            });
        });
    }
}
