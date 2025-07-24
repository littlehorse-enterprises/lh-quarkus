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

## Publish Locally

```shell
./gradlew publishToMavenLocal
```

> Optionally you can pass `-Pversion=<version>`

## Apply Code Style

```shell
./gradlew spotlessApply
```

## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Extension Metadata](https://quarkus.io/guides/extension-metadata)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
- [Writing Native Application Tips](https://quarkus.io/guides/writing-native-applications-tips)
