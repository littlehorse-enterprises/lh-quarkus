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

## Passing Configurations

## Creating a Task

Create a class and add the annotation `@LHTask`. Then, you can specify a Task Method using the `@LHTaskMethod` annotation.

```java
@LHTask
public class PrintTask {

    @LHTaskMethod("print")
    public void print(String message) {
        System.out.println(message);
    }
}
```

And that is it, Quarkus will take care of starting and stopping your task.

By default `@LHTask` is created as `@Singleton`, but you can change it adding `@Dependent`.

Supported scopes:

| Annotation   | Instances               |
|--------------|-------------------------|
| `@Singleton` | One per application     |
| `@Dependent` | One per injection point |


> More about task at: [Your First Task Worker](https://littlehorse.io/docs/getting-started/your-first-task-worker)
> and [Tasks and Task Workers](https://littlehorse.io/docs/server/concepts/tasks).

## Registering a Workflow

## Registering User Tasks

## Enabling Task Health Checks

## Default Injectable Objects

## Reactive RESTful Endpoint

## Native Build

## Tests
