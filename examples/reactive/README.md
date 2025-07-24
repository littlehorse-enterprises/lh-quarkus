# Reactive Example

This example shows you how to use the class `LittleHorseReactiveStub`.

`LittleHorseReactiveStub` allows you tu use the Quarkus Reactive Semantic.

```java
@ApplicationScoped
public class ReactiveService {
    private final LittleHorseReactiveStub reactiveStub;

    public ReactiveService(LittleHorseReactiveStub reactiveStub) { // 1
        this.reactiveStub = reactiveStub;
    }

    public Uni<String> runWf(String id, String message) { // 2
        String wfRunId =
                StringUtils.isBlank(id) ? UUID.randomUUID().toString().replace("-", "") : id;

        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName(ReactiveWorkflow.REACTIVE_WORKFLOW)
                .putVariables(ReactiveWorkflow.MESSAGE_VARIABLE, LHLibUtil.objToVarVal(message))
                .setId(wfRunId)
                .build();

        return reactiveStub
                .runWf(request) // 3
                .map(wfRun -> AwaitWorkflowEventRequest.newBuilder()
                        .addEventDefIds(WorkflowEventDefId.newBuilder()
                                .setName(ReactiveWorkflow.NOTIFY_EVENT))
                        .setWfRunId(wfRun.getId())
                        .build())
                .chain(reactiveStub::awaitWorkflowEvent) // 4
                .map(wfEvent -> wfEvent.getId().getWfRunId().getId());
    }
}
```

1. Firsts you have to inject a `io.littlehorse.quarkus.reactive.LittleHorseReactiveStub`.
2. The method will return a `io.smallrye.mutiny.Uni` object.
3. The methods of `LittleHorseReactiveStub` are reactive `reactiveStub.runWf(request)`.
4. You can chain multiple reactive methods.

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-reactive:quarkusDev
```

Send a request without id:

```shell
http :8080/hello message=="Hello Anakin"
```

Send a request with id:

```shell
http :8080/hello message=="Hello Leia" id==my-message-1
```

Check workflows:

```shell
lhctl search wfRun reactive-workflow
```
