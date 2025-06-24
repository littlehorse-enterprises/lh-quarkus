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
