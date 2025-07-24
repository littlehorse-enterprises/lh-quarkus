# Integration Tests Example

This example shows you how to run LH and perform Integration Tests.

## Running the Example

Execute tests:

```shell
./gradlew example-integration-tests:quarkusIntTest
```

Execute native tests:

```shell
./gradlew example-integration-tests:quarkusIntTest -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
```
