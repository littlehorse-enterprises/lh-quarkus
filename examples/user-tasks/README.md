# User Tasks Example

This example shows you how to use LH and User tasks on quarkus.

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-user-tasks:quarkusDev
```

Run workflow:

```shell
lhctl run execute-order-66 executor Anakin
```

Go to http://localhost:3000/ and get the `userTaskGuid`.

Check user tasks:

```shell
lhctl execute userTaskRun <wfRunId> <userTaskGuid>
```
