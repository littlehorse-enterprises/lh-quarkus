# User Tasks Example

This example shows you how to use LH and User tasks on quarkus.
Annotating a class as `@LHUserTaskForm` is enough to make LH Quarkus extension
to register it at runtime.

```java
@LHUserTaskForm(ApproveForm.APPROVE_USER_TASK)
public class ApproveForm {

    public static final String APPROVE_USER_TASK = "approve-user-task";

    @UserTaskField(
            displayName = "Approved?",
            description = "Reply 'true' if this is an acceptable request.")
    public boolean isApproved;
}
```

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-user-tasks:quarkusDev
```

Run workflow:

```shell
lhctl run execute-order-66 executor Anakin
```

Go to http://localhost:3000/ and get the `userTaskGuid`.

Check user tasks:

```shell
lhctl execute userTaskRun <wfRunId> <userTaskGuid>
```
