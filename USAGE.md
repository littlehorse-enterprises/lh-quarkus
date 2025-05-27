# Usage

## Table of Content

<!-- TOC -->
* [Usage](#usage)
  * [Table of Content](#table-of-content)
  * [Default Beans](#default-beans)
  * [Creating a Task](#creating-a-task)
  * [Registering a Workflow](#registering-a-workflow)
  * [Registering User Tasks](#registering-user-tasks)
  * [LittleHorse Clients](#littlehorse-clients)
  * [Enabling Task Health Checks](#enabling-task-health-checks)
  * [Native Build](#native-build)
  * [Tests](#tests)
<!-- TOC -->

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
    public LittleHorseBlockingStub customBlockingStub(LHConfig config) {
        return config.getBlockingStub();
    }

    @Produces
    @Singleton
    public LittleHorseFutureStub customFutureStub(LHConfig config) {
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

As well as with tasks, for workflows you have to create a class and add the `@LHWorkflow` annotation.
Additionally, you have to implement the `LHWorkflowDefinition` interface.

```java
@LHWorkflow("greetings")
public class GreetingsWorkflow implements LHWorkflowDefinition {

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name");
        wf.execute("greetings-task", name);
    }
}
```

Quarkus will register the [WfSpec](https://littlehorse.io/docs/server/concepts/workflows#the-wfspec) when starting the application.

Another way to register workflows is creating a `WorkflowProducer` class, example:

```java
@ApplicationScoped
public class WorkflowProducer {

    @LHWorkflow("hello-world")
    public void helloWorld(WorkflowThread wf) {
        wf.execute("print", "Hello World!");
    }

    @LHWorkflow("test")
    public void test(WorkflowThread wf) {
        wf.execute("print", "This is a test!");
    }
}
```

As you can see in the example above, you can add methods as workflows, this could be useful in some cases.

A final example with both task and workflow:

```java
@LHTask
public class HelloWorldWorker {

    @LHWorkflow("hello-world")
    public void helloWorldWorkflow(WorkflowThread wf) {
        wf.execute("hello-world", wf.declareStr("name"));
    }

    @LHTaskMethod("hello-world")
    public void helloWorldTask(String name) {
        System.out.println("Hello " + name);
    }
}
```

More about workflows at: [Your First WfSpec](https://littlehorse.io/docs/getting-started/your-first-wfspec)
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

## LittleHorse Clients

In case you are developing a [REST application](https://quarkus.io/guides/rest),
you can inject a `LittleHorseBlockingStub` client:

```java
@Path("/hello")
public class GreetingsResource {

    @Inject
    private LittleHorseBlockingStub blockingStub;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("name") String name) {
        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName("greetings")
                .putVariables("name", LHLibUtil.objToVarVal(name)).build();
        return blockingStub.runWf(request).getId().getId();
    }
}
```

In case you are developing a [Reactive Application](https://quarkus.io/guides/getting-started-reactive),
you can inject a `LittleHorseFutureStub` into your REST resource, and use `Uni.createFrom().future()`
to create an object `io.smallrye.mutiny.Uni`:

```java
@Path("/hello")
public class GreetingsResource {

    @Inject
    private LittleHorseFutureStub futureStub;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello(@QueryParam("name") String name) {
        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName("greetings")
                .putVariables("name", LHLibUtil.objToVarVal(name)).build();
        return Uni.createFrom()
                .future(futureStub.runWf(request))
                .map(wfRun -> wfRun.getId().getId());
    }
}
```

## Enabling Task Health Checks

Add `quarkus-smallrye-health` to your project.

```groovy
implementation "io.quarkus:quarkus-smallrye-health"
```

This extension will automatically add all [LHTaskWorker::healthStatus](https://github.com/littlehorse-enterprises/littlehorse/blob/master/sdk-java/src/main/java/io/littlehorse/sdk/worker/LHTaskWorker.java#L254)
to the quarkus health checks.

To disable this feature, you have to pass `quarkus.littlehorse.health.enabled=false`
config at build time (either `jar` or `native`). Example:

```shell
./gradlew build -Dquarkus.littlehorse.health.enabled=false
```

More about quarkus healthy checks at: [Smallrey Health](https://quarkus.io/guides/smallrye-health).

## Native Build

This extension fully supports native build.

Run next command in your project root folder:

```shell
./gradlew build \
-Dquarkus.native.enabled=true \
-Dquarkus.package.jar.enabled=false
```

More at [Building a Native Executable](https://quarkus.io/guides/building-native-image).

## Tests

LH provides a [Testcontainer](https://testcontainers.com/) implementation to allow you to test your integrations.
This library is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-test-utils-container).

Add it into your dependencies:

```groovy
dependencies {
    testImplementation "io.littlehorse:littlehorse-test-utils-container:${lhVersion}"
}
```

Then we need to create a [QuarkusTestResourceLifecycleManager](https://quarkus.io/guides/getting-started-testing#launching-containers)
class, and start LittleHorse:

```java
public class ContainersTestResource implements QuarkusTestResourceLifecycleManager {

    private final LittleHorseCluster cluster = LittleHorseCluster.newBuilder()
            .withKafkaImage("apache/kafka:4.0.0")
            .withLittlehorseImage("ghcr.io/littlehorse-enterprises/littlehorse/lh-server:latest")
            .build();

    @Override
    public Map<String, String> start() {
        cluster.start();
        return cluster.getClientConfig();
    }

    @Override
    public void stop() {
        cluster.stop();
    }
}
```

Add the test resource at your test:

```java
@QuarkusTest
@QuarkusTestResource(ContainersTestResource.class)
class YourTest {

    @Inject
    LittleHorseBlockingStub blockingStub;

    @Test
    void yourTest() {

    }
}
```

As you can see in the example above, you can inject a blocking stub client into the test.

It is highly recommended to add [Awaitility](http://www.awaitility.org/) and [REST Assured](https://rest-assured.io/)
libraries:

```groovy
dependencies {
    testImplementation "io.rest-assured:rest-assured:${restAssuredVersion}"
    testImplementation "org.awaitility:awaitility:${awaitilityVersion}"
}
```

For a test example go to the [src](src) folder.

More about tests at: [Testing Your Quarkus Application](https://quarkus.io/guides/getting-started-testing).
