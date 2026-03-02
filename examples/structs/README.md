# Structs Example

This example shows you how to run LH tasks and register a workflow with structs.
A `StructDef` is a user-definable schema for structured objects in LittleHorse.
Getting started with structs is as simple as creating a model object and annotating
it with `@LHStructDef`.
More at https://littlehorse.io/docs/server/concepts/structdefs.

Example:
```java
@LHStructDef("person")
public class Person {
    private String firstName;
    private String lastName;

    public Person() {}

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

Then it's just a matter of using it in your workflow or tasks:

```java
@LHWorkflow("my-workflow")
public void workflow(WorkflowThread wf) {
    WfRunVariable person = wf.declareStruct("person", Person.class).required();
    wf.execute("my-task", person);
}

@LHTaskMethod("my-task")
public void myTask(Person person) {
    System.out.println(person);
}
```

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-struct:quarkusDev
```

Run workflow:

```shell
http :8080/tickets vehicleMake=BARC vehicleModel=Speeder licensePlateNumber=1HGCM82633A004352 createdAt="$(date -u +'%Y-%m-%dT%H:%M:%SZ')"
```

Check workflows:

```shell
lhctl search wfRun issue-parking-ticket
```
