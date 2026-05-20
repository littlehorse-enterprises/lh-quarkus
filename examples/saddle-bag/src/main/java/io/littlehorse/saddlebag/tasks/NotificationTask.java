package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.saddle.config.LHRequiredConfig;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

@LHTask
@LHRequiredConfig(value = "notification.service.url", description = "Notification service base URL")
@LHRequiredConfig(
        value = "notification.service.api-key",
        description = "API key for the notification service",
        secret = true)
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
