# Saddle Bag Example

This example shows you how to use the Saddle Bag extension to package task workers as self-describing Docker images.

The Saddle Bag extension scans `@LHTask` classes at build time and produces a manifest describing all tasks, their inputs/outputs, struct definitions, and required configurations. Use `@LHTaskConfig` to declare external configurations your tasks require.

```java
@LHTask
@LHTaskConfig(value = "notification.service.url", description = "Notification service base URL")
@LHTaskConfig(
        value = "notification.service.api-key",
        description = "API key for the notification service",
        sensitive = true)
@LHTaskConfig(
        value = "notification.service.timeout-ms",
        description = "Timeout in milliseconds for notification service requests",
        defaultValue = "5000")
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
```

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-saddle-bag:quarkusDev
```

Build the saddle bag descriptor file in the specified format:

```shell
./gradlew example-saddle-bag:build
```

The generated manifest will be available in the build output directory.
