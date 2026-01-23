# Basic Example

This example shows you how to run LH tasks and register a workflow.

This example uses `@LHWorkflow` on a method within a class annotated as `@LHTask`.
The workflow executes two tasks: `greetings` and `print`. The tasks have their corresponding `@LHTaskMethod` methods.

```java
@LHTask
public class GreetingsTask {

    public static final String GREETINGS_TASK = "greetings";
    public static final String PRINT_TASK = "print";
    public static final String NAME_VARIABLE = "name";
    public static final String GREETINGS_WORKFLOW = "greetings";

    @LHWorkflow(GREETINGS_WORKFLOW)
    public void greetingsWorkflow(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr(NAME_VARIABLE);
        TaskNodeOutput message = wf.execute(GREETINGS_TASK, name);
        wf.execute(PRINT_TASK, message);
    }

    @LHTaskMethod(GREETINGS_TASK)
    public String greetings(String name) {
        return "Hello %s".formatted(name);
    }

    @LHTaskMethod(PRINT_TASK)
    public void print(String message) {
        System.out.println(message);
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
./gradlew example-basic:quarkusDev
```

Run workflow:

```shell
lhctl run greetings name Anakin
```

Check workflows:

```shell
lhctl search wfRun greetings
```
