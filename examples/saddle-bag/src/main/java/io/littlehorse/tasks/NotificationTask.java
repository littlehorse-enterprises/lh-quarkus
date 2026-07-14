package io.littlehorse.tasks;

import io.littlehorse.quarkus.saddle.config.LHTaskConfig;
import io.littlehorse.quarkus.saddle.config.LHTaskConfig.LHTaskConfigType;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

@LHTask
@LHTaskConfig(
        value = "notification.service.url",
        description = "Notification service base URL",
        type = LHTaskConfigType.STR)
@LHTaskConfig(
        value = "notification.service.api-key",
        description = "API key for the notification service",
        sensitive = true,
        type = LHTaskConfigType.STR)
@LHTaskConfig(
        value = "notification.service.timeout-ms",
        description = "Timeout in milliseconds for notification service requests",
        defaultValue = "5000",
        type = LHTaskConfigType.INT)
public class NotificationTask {

    public static final String SEND_NOTIFICATION = "${task.send-notification.name}";

    @LHTaskMethod(
            value = SEND_NOTIFICATION,
            description = "Sends a notification to the given recipient using workflow context")
    public String sendNotification(String recipient, String message, WorkerContext context) {
        return "Sent to %s from wfRun %s: %s"
                .formatted(recipient, context.getWfRunId().getId(), message);
    }
}
