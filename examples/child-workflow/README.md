# Child Workflow Example

This example shows you how to register 2 workflows and at the same time set a parent workflow.

To define a parent workflow for a child workflow you have to set the `parent` field of `@LHWorkflow`, for example: `@LHWorkflow(value = CHILD_WF, parent = PARENT_WF)`.

```java
@LHTask
public class ChildWorkflow {
    public static final String GREETINGS_TASK = "child-wf-greetings";
    public static final String NAME_VARIABLE = "name";
    public static final String PARENT_WF = "parent-wf";
    public static final String CHILD_WF = "child-wf";

    @LHWorkflow(PARENT_WF)
    public void parent(WorkflowThread wf) {
        wf.execute(GREETINGS_TASK, PARENT_WF, wf.declareStr(NAME_VARIABLE).asPublic());
    }

    @LHWorkflow(value = CHILD_WF, parent = PARENT_WF)
    public void child(WorkflowThread wf) {
        wf.execute(GREETINGS_TASK, CHILD_WF, wf.declareStr(NAME_VARIABLE).asInherited());
    }

    @LHTaskMethod(GREETINGS_TASK)
    public void greetings(String wfName, String name) {
        System.out.printf("Hello %s from %s %n", name, wfName);
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
./gradlew example-child-workflow:quarkusDev
```

Run workflow:

```shell
lhctl run --wfRunId my-parent-wf parent-wf name Anakin
```

Run child workflow:

```shell
lhctl run --parentWfRunId my-parent-wf --wfRunId my-child-wf child-wf
```
