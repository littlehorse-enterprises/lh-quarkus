# Development

## Dependencies

- [docker](https://docs.docker.com/engine/install/)
- [java](https://sdkman.io/jdks#graalce)

## Utilities

- [httpie](https://httpie.io/)
- [pre-commit](https://pre-commit.com/)

## Setup

Install pre-commit hooks:

```shell
pre-commit install
```

Run LH:

```shell
docker compose up -d
```

| Container             | Port |
|-----------------------|------|
| LittleHorse           | 2023 |
| Kafka                 | 9092 |
| LittleHorse Dashboard | 3000 |

## Unit Tests

```shell
./gradlew test
```

## Integration Tests

```shell
./gradlew quarkusIntTest
```

## Native Tests

```shell
./gradlew testNative
```

## Publish Locally

```shell
./gradlew publishToMavenLocal
```

> Optionally you can pass `-Pversion=<version>`

## Run

```shell
./gradlew quarkusDev
```

```shell
http -v :8080/hello name==Luck id==my-workflow-1
```

```shell
http -v :8080/hello/reactive name==Anakin
```

```shell
lhctl run greetings name Leia
```

## Run User Task

```shell
./gradlew quarkusDev
```

```shell
lhctl run execute-order-66 executor Anakin
```

```shell
lhctl execute userTaskRun <wfRunId> <userTaskGuid>
```

## Run Native

```shell
./gradlew build \
-Dquarkus.native.enabled=true \
-Dquarkus.package.jar.enabled=false \
-Dquarkus.package.output-name=example
```

```shell
./build/example-runner
```

## Log level

```shell
./build/example-runner -Dquarkus.log.level=DEBUG
```

```shell
./gradlew quarkusDev -Dquarkus.log.level=DEBUG
```

## Health Checks

```shell
http :8080/q/health
```

```shell
http :8080/q/health/ready
```

```shell
http :8080/q/health/live
```

## Apply Code Style

```shell
./gradlew spotlessApply
```

## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
- [Writing Native Apps](https://quarkus.io/guides/writing-native-applications-tips)
