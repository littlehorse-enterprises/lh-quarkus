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

In case you need to override the [default beans](https://quarkus.io/guides/cdi-reference#default_beans)
provided by the extension just add new ones. Example:

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

## Creating a Worker

## Registering a Workflow

## Registering User Tasks

## Enabling Worker Health Checks

## Default Injectable Objects

## Reactive RESTful Endpoint

## Native Build

## Tests
