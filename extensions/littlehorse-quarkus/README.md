# LittleHorse Quarkus Extension

This is the base Quarkus extension for [LittleHorse](https://littlehorse.io/).

# Table of Content

<!-- TOC -->
* [LittleHorse Quarkus Extension](#littlehorse-quarkus-extension)
* [Table of Content](#table-of-content)
* [Installation](#installation)
* [Usage](#usage)
  * [Default Beans](#default-beans)
  * [Creating a Task](#creating-a-task)
  * [Registering a Workflow](#registering-a-workflow)
  * [Registering User Tasks](#registering-user-tasks)
  * [LittleHorse Clients](#littlehorse-clients)
  * [Enabling Task Health Checks](#enabling-task-health-checks)
  * [Native Build](#native-build)
  * [Tests](#tests)
* [Troubleshooting](#troubleshooting)
  * [Transactional LHTaskMethod](#transactional-lhtaskmethod)
  * [Missing LHTaskMethod Annotation](#missing-lhtaskmethod-annotation)
* [Configurations](#configurations)
  * [Passing Configurations](#passing-configurations)
  * [Expressions Expansion](#expressions-expansion)
  * [LittleHorse Client Configurations](#littlehorse-client-configurations)
    * [Client](#client)
    * [Task Worker](#task-worker)
  * [LittleHorse Extension Configurations](#littlehorse-extension-configurations)
    * [Buildtime Configurations](#buildtime-configurations)
    * [Runtime Configurations](#runtime-configurations)
<!-- TOC -->

# Installation

<a href="https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.littlehorse/littlehorse-quarkus?label=latest"></a>

This extension is available at [Maven Central](https://central.sonatype.com/artifact/io.littlehorse/littlehorse-quarkus).

Gradle:

```groovy
implementation "io.littlehorse:littlehorse-quarkus:${lhVersion}"
```

Maven:

```xml
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus</artifactId>
    <version>${lhVersion}</version>
</dependency>
```

# Usage

## Default Beans

This extension provides [default beans](https://quarkus.io/guides/cdi-reference#default_beans) such as:

- `io.littlehorse.sdk.common.config.LHConfig`: This class is used to configure gRPC clients and LH tasks.
- `io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub`: LH gRPC blocking client.
- `io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub`: LH gRPC async client.
- `io.littlehorse.quarkus.reactive.LittleHorseReactiveStub`: LH gRPC reactive client.

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

A class example with both task and workflow:

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

It is also possible to configure the workflow. In the next example we add a **parent wf**:

```java
@LHTask
public class ChildWorkflow {
    public static final String GREETINGS_TASK = "greetings";
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

        return blockingStub.runWf(request)
                .getId().getId();
    }
}
```

In case you are developing a [Reactive Application](https://quarkus.io/guides/getting-started-reactive),
you can inject a `LittleHorseReactiveStub`:

```java
@Path("/hello")
public class GreetingsResource {

    @Inject
    private LittleHorseReactiveStub reactiveStub;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello(@QueryParam("name") String name) {
        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName("greetings")
                .putVariables("name", LHLibUtil.objToVarVal(name))
                .build();

        return reactiveStub.runWf(request)
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
            .withKafkaImage("apache/kafka:latest")
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

More about tests at: [Testing Your Quarkus Application](https://quarkus.io/guides/getting-started-testing).

# Troubleshooting

## Transactional LHTaskMethod

It is very common to persist data into relational databases inside `@LHTaskMethods`.
It is also very common to use framework like [Hibernate](https://quarkus.io/guides/hibernate-orm),
or, in the case of Quarkus, [Panache](https://quarkus.io/guides/hibernate-orm-panache).

As it is now, every `@LHTaskMethod` needs an [LHTaskWorker](https://littlehorse.io/docs/server/developer-guide/task-worker-development)
which are created and managed by the LH Quarkus extension. An LHTaskWorker runs inside
its own thread and outside the Quarkus CDI (read more at [Manage Non-CDI Service](https://quarkus.io/guides/writing-extensions#manage-non-cdi-service)).
Therefore, you could receive `ContextNotActiveException`, example:

```
jakarta.enterprise.context.ContextNotActiveException: Cannot use the EntityManager/Session because neither
a transaction nor a CDI request context is active. Consider adding @Transactional to your method to
automatically activate a transaction, or @ActivateRequestContext if you have valid reasons not to use transactions.
```

To avoid this, you should add `@Transactional` to your service method, example:

```java
@ApplicationScoped
public class MyService {
    @Inject
    private MyRepository repository;

    @Transactional
    public void saveEntity(MyEntity myEntity) {
        repository.persist(myEntity);
    }
}
```

And, you should invoke this service in your task, example:

```java
@LHTask
public class MyTask {
    @Inject
    private MyService service;

    @LHTaskMethod("my-task")
    public void persist(MyEntity myEntity) {
        service.saveEntity(name);
    }
}
```

## Missing LHTaskMethod Annotation

In LH an [LHTaskWorker](https://littlehorse.io/docs/server/developer-guide/task-worker-development)
uses [Reflection](https://www.oracle.com/technical-resources/articles/java/javareflection.html)
to identify the `@LHTaskMethod` to run. Quarkus lists these classes
and then register them for reflection (more at [Registering for reflection](https://quarkus.io/guides/writing-native-applications-tips#registering-for-reflection)).

But, there are some annotations which could change this behavior. An example of this
is adding the annotation `@Transactional` to a `@LHTaskMethod`:

```java
@LHTask
public class MyTask {
    @Inject
    private MyService service;

    @LHTaskMethod("my-task")
    @Transactional
    public void persist(MyEntity myEntity) {
        service.saveEntity(name);
    }
}
```

When adding the annotation `@Transactional` quarkus creates a
[Class Proxy](https://quarkus.io/guides/cdi#client_proxies),
making it impossible for LH to find the `@LHTaskMethod` under the hood.
You will receive a `TaskSchemaMismatchError`, example:

```
io.littlehorse.sdk.common.exception.TaskSchemaMismatchError: Couldn't find annotated
@LHTaskMethod for taskDef greetings on class io.littlehorse.tasks.MyTask_Subclass
```

For that reason, it is recommended to move the annotation `@Transactional` into a service class.

# Configurations

More about configurations at: [Configuration Reference Guide](https://quarkus.io/guides/config-reference).

## Passing Configurations

Quarkus supports multiple configuration sources.

Some examples could be:

* **Environment Variables:** Adding a variable (ex: `LHC_API_PORT=2023`) to the OS/Container.
* **System Properties:** Adding a `-D` property (ex: `-Dlhc.api.port=2023`) to the command line when running the
  artifact.
* **Property File:** Adding a property entry (ex: `lhc.api.port=2023`) into the `application.properties` file.

## Expressions Expansion

This extension supports [Expressions Expansion](https://smallrye.io/smallrye-config/Main/config/expressions/)
for configurations.

An expression string is a mix of plain strings and expression segments, which are wrapped by the sequence: `${ … }`.
Additionally, the Expression Expansion engine supports the following segments:

`${expression:value}` - Provides a default value after the `:` if the expansion doesn’t find a value.
`${my.prop${compose}}` - Composed expressions. Inner expressions are resolved first.
`${my.prop}${my.prop}` - Multiple expressions.

Examples:

```java
@LHWorkflow(
        value = "${my.wf.name}",
        defaultTaskExponentialBackoffRetry =
                @LHExponentialBackoffRetry(
                        baseIntervalMs = "${retry.base.interval.ms}",
                        maxDelayMs = "${retry.max.delay.ms}",
                        multiplier = "${retry.multiplier}"))
public class GreetingsWorkflow implements LHWorkflowDefinition {

    @ConfigProperty(name = "my.task.name")
    String taskPrintName;

    @Override
    public void define(WorkflowThread wf) {
        wf.execute(taskPrintName, wf.declareStr("name"));
    }
}

@LHTask
public class PrintTask {

    @LHTaskMethod("${my.task.name}")
    public void print(String name) {
        System.out.println("Hello " + name);
    }
}
```

## LittleHorse Client Configurations

More about LH Configurations at: [Workers/Clients Configurations](https://littlehorse.io/docs/server/operations/client-configuration)
and [Configuring the Clients](https://littlehorse.io/docs/server/developer-guide/client-configuration).

### Client

``lhc.api.host``
The bootstrap host for the LittleHorse Server.

* Type: string
* Importance: high

``lhc.api.port``
The bootstrap port for the LittleHorse Server.

* Type: int
* Importance: high

``lhc.api.protocol``
The bootstrap protocol for the LittleHorse Server.

* Type: string
* Default: PLAINTEXT
* Valid Values: [PLAINTEXT, TLS]
* Importance: high

``lhc.tenant.id``
Tenant ID which represents a logically isolated environment within LittleHorse.

* Type: string
* Default: default
* Importance: medium

``lhc.ca.cert``
Optional location of CA Cert file that issued the server side certificates. For TLS and mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.client.cert``
Optional location of Client Cert file for mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.client.key``
Optional location of Client Private Key file for mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.grpc.keepalive.time.ms``
Time in milliseconds to configure keepalive pings on the grpc client.

* Type: long
* Default: 45000 (45 seconds)
* Importance: low

``lhc.grpc.keepalive.timeout.ms``
Time in milliseconds to configure the timeout for the keepalive pings on the grpc client.

* Type: long
* Default: 5000 (5 seconds)
* Importance: low

``lhc.oauth.access.token.url``
Optional Access Token URL provided by the OAuth Authorization Server. Used by the Worker to obtain a token using client credentials flow.

* Type: string
* Default: null
* Importance: low

``lhc.oauth.client.id``
Optional OAuth2 Client Id. Used by the Worker to identify itself at an Authorization Server. Client Credentials Flow.

* Type: string
* Default: null
* Importance: low

``lhc.oauth.client.secret``
Optional OAuth2 Client Secret. Used by the Worker to identify itself at an Authorization Server. Client Credentials Flow.

* Type: password
* Default: null
* Importance: low

### Task Worker

``lhw.num.worker.threads``
The number of worker threads to run. It allows you to improve the task execution's performance parallelizing the tasks assigned to this worker.

* Type: int
* Default: 8
* Importance: medium

``lhw.task.worker.id``
Unique identifier for the Task Worker. It is used by the LittleHorse cluster to load balance the worker requests across all servers. Additionally, it is journalled on every `TaskAttempt` run by the Task Worker, so that you can more easily debug where a request was executed from. It is recommended to set this value for production environments.

* Type: string
* Default: a random value
* Importance: medium

``lhw.task.worker.version``
Optional version identifier. Intended to be informative. Useful when you're running different versions of a worker. Along with the `lhw.task.worker.id`, this is journalled on every `TaskAttempt`.

* Type: string
* Default: ""
* Importance: medium

## LittleHorse Extension Configurations

### Buildtime Configurations

``quarkus.littlehorse.health.enabled``
Enables health checks for the running `LHTaskWorker` list.

* Type: boolean
* Default: true
* Importance: low

### Runtime Configurations

``quarkus.littlehorse.tasks.start.enabled``
Automatically starts all `LHTaskWorker` found.

* Type: boolean
* Default: true
* Importance: medium

``quarkus.littlehorse.tasks.register.enabled``
Registers all `TaskDef` found when starting the application.

* Type: boolean
* Default: true
* Importance: medium

``quarkus.littlehorse.workflows.register.enabled``
Registers all `WfSpec` found when starting the application.

* Type: boolean
* Default: true
* Importance: medium

``quarkus.littlehorse.user-tasks.register.enabled``
Registers all `UserTaskDef` found when starting the application.

* Type: boolean
* Default: true
* Importance: medium
