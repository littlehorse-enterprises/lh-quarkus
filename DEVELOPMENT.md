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

Execute tests:

```shell
./gradlew integration-tests:quarkusIntTest
```

Execute native tests:

```shell
./gradlew integration-tests:quarkusIntTest -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
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

## Proxy

Execute it:

```shell
./gradlew proxy:quarkusDev
```

Get server version:

```shell
http :8080/proxy/version
```

> Equivalent to `lhctl version`

Get wfSpec:

```shell
http :8080/proxy/tenants/default/wf-specs/greetings
```

> Equivalent to `lhctl get wfSpec greetings`

Pass the token in the http request ([Bearer auth](https://httpie.io/docs/cli/bearer-auth)):

```shell
http -A bearer -a $TOKEN :8080/proxy/tenants/default/wf-specs/greetings
```

## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Extension Metadata](https://quarkus.io/guides/extension-metadata)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
- [Writing Native Application Tips](https://quarkus.io/guides/writing-native-applications-tips)
