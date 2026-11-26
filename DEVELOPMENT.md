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
./gradlew dockerComposeUp
```

Stop LH:

```shell
./gradlew dockerComposeDown
```

| Container             | Port |
| --------------------- | ---- |
| LittleHorse           | 2023 |
| Kafka                 | 9092 |
| LittleHorse Dashboard | 3000 |

## Unit Tests

```shell
./gradlew test
```

## Integration Tests

Execute tests:

```shell
./gradlew integration-tests-littlehorse-quarkus:quarkusIntTest integration-tests-littlehorse-saddle-bag:quarkusIntTest
```

Execute native tests:

```shell
./gradlew integration-tests-littlehorse-quarkus:quarkusIntTest integration-tests-littlehorse-saddle-bag:quarkusIntTest -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
```

## Publish Locally

```shell
./gradlew publishToMavenLocal
```

> The LH version is defined in `gradle.properties` (`version` property) and passed automatically to docker-compose via the Gradle tasks.

## Apply Code Style

```shell
./gradlew spotlessApply
```

## Saddle Bag

Run the example:

```shell
./gradlew example-saddle-bag:quarkusDev
```

Generate the saddle bag manifest:

```shell
./gradlew example-saddle-bag:build
```

> The manifest is generated under the example's `build/saddle-bag/` directory.

Saddle bag Docker images are built and published with the
[publish-saddle-bag](https://github.com/littlehorse-enterprises/publish-saddle-bag) GitHub Action.

## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Extension Metadata](https://quarkus.io/guides/extension-metadata)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
- [Writing Native Application Tips](https://quarkus.io/guides/writing-native-applications-tips)
