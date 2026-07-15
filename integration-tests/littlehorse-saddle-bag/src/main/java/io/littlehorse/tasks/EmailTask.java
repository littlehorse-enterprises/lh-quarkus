package io.littlehorse.tasks;

import io.littlehorse.quarkus.saddle.config.LHTaskConfig;
import io.littlehorse.quarkus.saddle.config.LHTaskConfig.LHTaskConfigType;
import io.littlehorse.quarkus.saddle.exception.LHTaskMethodException;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
@LHTaskConfig(
        value = "smtp.host",
        description = "SMTP server hostname",
        type = LHTaskConfigType.STR)
@LHTaskConfig(
        value = "smtp.password",
        description = "SMTP server password",
        sensitive = true,
        type = LHTaskConfigType.STR)
public class EmailTask {

    public static final String SEND_EMAIL_TASK = "${task.send-email.name}";

    @LHTaskMethod(value = SEND_EMAIL_TASK, description = "Sends an email to a recipient")
    @LHTaskConfig(
            value = "smtp.port",
            description = "SMTP server port",
            defaultValue = "587",
            type = LHTaskConfigType.INT)
    @LHTaskMethodException(
            name = "invalid-address",
            description = "The recipient email address is invalid")
    public boolean sendEmail(String to, String body) {
        return to != null && to.contains("@") && body != null;
    }
}
