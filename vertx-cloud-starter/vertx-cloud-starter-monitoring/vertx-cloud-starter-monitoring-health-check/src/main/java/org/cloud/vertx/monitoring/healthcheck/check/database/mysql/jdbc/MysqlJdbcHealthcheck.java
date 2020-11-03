package org.cloud.vertx.monitoring.healthcheck.check.database.mysql.jdbc;

import org.cloud.vertx.monitoring.healthcheck.check.Healthcheck;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.jdbc.JDBCClient;
import org.cloud.vertx.monitoring.healthcheck.check.SubHealthcheck;

/**
 * mysql jdbc健康检测
 *
 * @author frank
 */
public class MysqlJdbcHealthcheck extends SubHealthcheck implements Healthcheck {
    private static final String PRE_REGISTER_NAME = "database.mysql.jdbc";

    public MysqlJdbcHealthcheck(HealthCheckHandler healthCheckHandler) {
        super(healthCheckHandler);
    }

    @Override
    public void check(JDBCClient jdbcClient, String registerName) {
        healthCheckHandler.register(this.getRegisterName(registerName),
                promise -> jdbcClient.getConnection(connection -> {
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
