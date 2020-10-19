package org.cloud.verx.starter.health.check.database.mysql.client;

import org.cloud.verx.starter.health.check.Healthcheck;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.sqlclient.Pool;
import org.cloud.verx.starter.health.check.SubHealthcheck;

/**
 * mysql client健康检测
 *
 * @author frank
 */
public class MysqlClientHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "database.mysql.client";

    public MysqlClientHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(Pool pool, String registerName) {
        healthCheckHandler.register(this.getRegisterName(registerName),
                promise -> pool.getConnection(connection -> {
                    if (connection.failed()) {
                        promise.fail(connection.cause());
                    } else {
                        connection.result().close();
                        promise.complete(Status.OK());
                    }
                }));
    }

    @Override
    public String getRegisterName(String registerName) {
        return this.PRE_REGISTER_NAME + (registerName != null ? "." + registerName : "");
    }
}
