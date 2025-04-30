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
./gradlew publishToMavenLocal -Pversion=<version>
```

## Code Style

Apply code style:

```shell
./gradlew spotlessApply
```

## Useful Commands

For more useful commands go to [COMMANDS.md](COMMANDS.md).
