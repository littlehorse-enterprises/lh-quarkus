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

## Troubleshooting

In case you receive this error:
```shell
BadRequestException (Status 400: {"message":"client version 1.32 is too old. Minimum supported API version is 1.44, please upgrade your client to a newer version"})
```

Run this command:
```shell
echo api.version=1.44 >> ~/.docker-java.properties
```
