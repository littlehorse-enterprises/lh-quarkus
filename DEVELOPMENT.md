# Development

## Dependencies

- docker
- java

## Utilities

- httpie
- jq
- pre-commit

## Setup

Install pre-commit hooks:

```shell
pre-commit install
```

## Tests

```shell
./gradlew test
```

## Publish Locally

Publish:

```shell
./gradlew publishToMavenLocal
```

> Optionally you can pas ` -Pversion=<version>`

## Code Style

Apply code style:

```shell
./gradlew spotlessApply
```

## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
