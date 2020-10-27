package org.cloud.vertx.starter.health.notice;

import io.vertx.core.json.JsonObject;

public interface NoticeService {
    void notice(JsonObject data);
}
