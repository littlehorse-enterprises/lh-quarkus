# User Tasks Example

This example registers an approval result StructDef and a UserTaskDef, assigns the
user task, and reads `isApproved` from the submitted Struct in the workflow.

Declare the submitted data as a named `StructDef`, then reference its class from
`@LHUserTaskDef`. The extension registers the StructDef first and uses the returned
ID as the UserTaskDef's minimum result schema version. Workflows are registered
after their UserTaskDefs.

```java
@LHStructDef("approval-result")
public class ApprovalResult {
    private Boolean approved;

    @LHStructField(name = "isApproved", description = "Whether the request is approved.")
    public Boolean getApproved() { return approved; }

    public void setApproved(Boolean approved) { this.approved = approved; }
}
```

```java
@LHUserTaskDef(value = ApproveUserTask.APPROVE_USER_TASK, result = ApprovalResult.class)
public class ApproveUserTask {
    public static final String APPROVE_USER_TASK = "approve-user-task";
}
```

Multiple UserTaskDefs can reference the same result class. The result must have
`@LHStructDef`; the application controls form layout and presentation.

The existing `quarkus.littlehorse.user-tasks.*.register.enabled` settings control
UserTaskDef registration. A referenced StructDef must also have registration
enabled. Set its `quarkus.littlehorse.structs.<name>.register.compatibility` to
`FULLY_COMPATIBLE_SCHEMA_UPDATES` when registering compatible schema changes.

## Running the Example

Start the server, Kafka, and Dashboard:

```shell
./gradlew dockerComposeUp
```

Start the application and keep it running:

```shell
./gradlew example-user-tasks:quarkusDev
```

In another terminal, run the workflow:

```shell
lhctl run execute-order-66 executor Anakin
```

Open http://localhost:3000/ to find the workflow's UserTaskRun ID, or search for it:

```shell
lhctl search userTaskRun --userTaskDefName approve-user-task --userId Anakin
```

Complete it using a matching `lhctl` build. The command prompts for the fields in
the run's pinned StructDef:

```shell
lhctl execute userTaskRun <wfRunId> <userTaskGuid>
```

Submit `true` for `isApproved` to print `Well done Anakin`, or `false` to print
`Very bad Anakin`. The workflow then completes.
