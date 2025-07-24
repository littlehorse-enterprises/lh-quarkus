# Basic Example

This example shows you how to run LH tasks and register a workflow.

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-basic:quarkusDev
```

Run workflow:

```shell
lhctl run greetings name Anakin
```

Check workflows:

```shell
lhctl search wfRun greetings
```
