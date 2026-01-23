# Rest Example

This example shows you how to use a gRPC LH client in a REST endpoint on quarkus.

The class `GreetingsService` is a bean, and it requires a blocking stub by constructor, example:

```java
private final LittleHorseBlockingStub blockingStub;

public GreetingsService(LittleHorseBlockingStub blockingStub) {
    this.blockingStub = blockingStub;
}
```

The LH quarkus extension already provides a `LittleHorseBlockingStub` to be used.

Then the `GreetingsResource` invokes the service `service.runWf(id, name)` which has the blocking stub
as attribute.

```java
public String runWf(String id, String name) {
    String wfRunId =
            StringUtils.isBlank(id) ? UUID.randomUUID().toString().replace("-", "") : id;

    RunWfRequest request = RunWfRequest.newBuilder()
            .setWfSpecName(GreetingsWorkflow.GREETINGS_WORKFLOW)
            .putVariables(GreetingsWorkflow.NAME_VARIABLE, LHLibUtil.objToVarVal(name))
            .setId(wfRunId)
            .build();

    return blockingStub.runWf(request).getId().getId();
}
```

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-rest:quarkusDev
```

Send a request without id:

```shell
http :8080/hello name=="Anakin"
```

Send a request with id:

```shell
http :8080/hello name=="Leia" id==my-id-1
```

Check workflows:

```shell
lhctl search wfRun greetings
```
