# Saddle Bag Example

This example shows you how to use the Saddle Bag extension to package task workers as self-describing Docker images.

The Saddle Bag extension scans `@LHTask` classes at build time and produces a manifest describing all tasks, their inputs/outputs, struct definitions, and required configurations. Use `@LHTaskConfig` (class level) to declare **global** external configurations, `@LHTaskMethodConfig` (method level) to declare configurations required by a single task method, and `@LHTaskMethodException` to declare the business exceptions a task method may throw.

```java
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
    @LHTaskMethodConfig(
            value = "notification.send.max-retries",
            description = "Maximum number of delivery attempts for a notification",
            defaultValue = "3",
            type = LHTaskConfigType.INT)
    @LHTaskMethodException(
            name = "recipient-unreachable",
            description = "The recipient could not be reached by the notification service")
    @LHTaskMethodException(
            name = "invalid-recipient",
            description = "The recipient address is malformed or not allowed")
    public String sendNotification(String recipient, String message, WorkerContext context) {
        return "Sent to %s from wfRun %s: %s"
                .formatted(recipient, context.getWfRunId().getId(), message);
    }
}
```

Class-level `@LHTaskConfig` values are emitted once under the top-level `configs` field (deduplicated across classes), while `@LHTaskMethodConfig` values are emitted under the owning task. A given `@LHTaskMethodConfig` key may only be used by one task method — shared configuration should use `@LHTaskConfig` instead.

Business exceptions are thrown from your task code via `LHTaskException` (or a subclass). Declaring them with `@LHTaskMethodException` lets consumers of the saddle bag know which `EXCEPTION`s a `WfSpec` can catch. See the [exception handling docs](https://littlehorse.io/docs/server/concepts/exception-handling) for details.


## Running the Example

Setup:

```shell
./gradlew dockerComposeUp
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
