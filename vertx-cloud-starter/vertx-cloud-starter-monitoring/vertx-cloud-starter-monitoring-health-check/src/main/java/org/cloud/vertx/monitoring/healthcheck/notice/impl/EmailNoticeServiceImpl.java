package org.cloud.vertx.monitoring.healthcheck.notice.impl;

import io.netty.util.internal.StringUtil;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import org.cloud.vertx.monitoring.healthcheck.notice.NoticeService;

/**
 * 健康检测邮件通知
 *
 * @author frank
 */
public class EmailNoticeServiceImpl implements NoticeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNoticeServiceImpl.class);
    MailClient mailClient;

    public EmailNoticeServiceImpl(Vertx vertx, JsonObject emailConfig) {
        MailConfig config = new MailConfig(emailConfig);
        mailClient = MailClient.create(vertx, config);
    }

    @Override
    public void notice(JsonObject data) {
        MailMessage message = new MailMessage();
        message.setFrom(data.getString("from"));
        message.setTo(data.getJsonArray("to").getList());
        if (data.containsKey("cc")) {
            message.setCc(data.getJsonArray("cc").getList());
        }

        String subject = data.getString("vertx.application.name");
        if (StringUtil.isNullOrEmpty(subject)) {
            subject = data.getString("subject");
        }
        message.setSubject(subject + ",ip:" + data.getString("ip"));
        message.setHtml(data.getString("html"));
        mailClient.sendMail(message)
                .onFailure(e -> {
                    LOGGER.error(data.toString(), e);
                });
    }
}
