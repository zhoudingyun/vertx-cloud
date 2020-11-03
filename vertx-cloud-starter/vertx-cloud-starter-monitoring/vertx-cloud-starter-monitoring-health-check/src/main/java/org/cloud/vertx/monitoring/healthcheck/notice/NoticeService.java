package org.cloud.vertx.monitoring.healthcheck.notice;

import io.vertx.core.json.JsonObject;

/**
 * 健康检测通知
 *
 * @author frank
 */
public interface NoticeService {
    void notice(JsonObject data);
}
