# Basic Example

This example shows you how to run LH tasks and register a workflow.

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-child-workflow:quarkusDev
```

Run workflow:

```shell
lhctl run --wfRunId my-parent-wf parent-wf name Anakin
```

Run child workflow:

```shell
lhctl run --parentWfRunId my-parent-wf --wfRunId my-child-wf child-wf
```
