# Usage

## Default Beans

This extension provides [default beans](https://quarkus.io/guides/cdi-reference#default_beans) such as:

- `io.littlehorse.sdk.common.config.LHConfig`: This class is used to configure gRPC clients and LH tasks.
- `io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub`: LH gRPC blocking client.
- `io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub`: LH gRPC async client.

Examples:

```java
@ApplicationScoped
public class MyService {

    private final LittleHorseBlockingStub blockingStub;

    public MyService(LittleHorseBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }
}
```

```java
@ApplicationScoped
public class MyService {

    @Inject
    private LittleHorseBlockingStub blockingStub;
}
```

In case you need to override the provided beans by the extension, just add new ones. Example:

```java
@ApplicationScoped
public class MyBeans {

    @Produces
    @Singleton
    public LHConfig customConfig(){
        return LHConfig.newBuilder().build();
    }

    @Produces
    @Singleton
    LittleHorseBlockingStub customBlockingStub(LHConfig config) {
        return config.getBlockingStub();
    }

    @Produces
    @Singleton
    LittleHorseFutureStub customFutureStub(LHConfig config) {
        return config.getFutureStub();
    }
}
```

More about dependency injections at:

- [Introduction to CDI](https://quarkus.io/guides/cdi)
- [CDI Reference](https://quarkus.io/guides/cdi-reference)

## Creating a Task

Create a class and add the annotation `@LHTask`. Then, you can specify a task method using the `@LHTaskMethod` annotation.

```java
@LHTask
public class GreetingsTask {

    @LHTaskMethod("greetings-task")
    public void print(String name) {
        System.out.println("Hello " + name);
    }
}
```

And that is it, Quarkus will take care of starting and stopping your task.
Additionally, Quarkus will register the [TaskDef](https://littlehorse.io/docs/server/concepts/tasks#register-the-taskdef-and-start-polling) when starting the application.

By default `@LHTask` is created as a `@Singleton` bean, but you can change it adding `@Dependent`.

Supported scopes:

| Annotation   | Instances               |
|--------------|-------------------------|
| `@Singleton` | One per application     |
| `@Dependent` | One per injection point |


More about tasks at: [Your First Task Worker](https://littlehorse.io/docs/getting-started/your-first-task-worker)
and [Tasks and Task Workers](https://littlehorse.io/docs/server/concepts/tasks).

## Registering a Workflow

As well as with tasks, for workflows you have to create a class and then add the `@LHWorkflow` annotation.
In LittleHorse workflows implement the `ThreadFunc` interface.

```java
@LHWorkflow("greetings")
public class GreetingsWorkflow implements ThreadFunc {

    @Override
    public void threadFunction(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name");
        wf.execute("greetings-task", name);
    }
}
```

Quarkus will register the [WfSpec](https://littlehorse.io/docs/server/concepts/workflows#the-wfspec) when starting the application.

More about workflows at: [Yor First WfSpec](https://littlehorse.io/docs/getting-started/your-first-wfspec)
and [Workflows](https://littlehorse.io/docs/server/concepts/workflows).

## Registering User Tasks

As well as the other beans, for a user task you have to add the annotation `@LHUserTaskForm`.
Then you can add any number of fields (`@UserTaskField`).

```java
@LHUserTaskForm("approve-user-task")
public class ApproveForm {

    @UserTaskField(
            displayName = "Approved?",
            description = "Reply 'true' if this is an acceptable request.")
    public boolean isApproved;
}
```

Quarkus will register the [UserTaskDef](https://littlehorse.io/docs/server/concepts/user-tasks#create-the-usertaskdef)
when starting the application.

More about user tasks at: [User Tasks](https://littlehorse.io/docs/server/concepts/user-tasks).

## Reactive RESTful Endpoint

## Enabling Task Health Checks

## Native Build

## Tests
