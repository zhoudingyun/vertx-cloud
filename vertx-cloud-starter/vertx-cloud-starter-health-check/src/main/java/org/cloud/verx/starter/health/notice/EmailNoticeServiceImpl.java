package org.cloud.verx.starter.health.notice;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import org.apache.commons.lang3.StringUtils;

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
        message.setTo(data.getString("to"));
        if (StringUtils.isNotBlank(data.getString("cc"))) {
            message.setCc(data.getString("cc"));
        }
        message.setText(data.getString("text"));
        message.setHtml(data.getString("html"));

        mailClient.sendMail(message)
                .onFailure(e -> {
                    LOGGER.error(data.toString(), e);
                });
    }
}
