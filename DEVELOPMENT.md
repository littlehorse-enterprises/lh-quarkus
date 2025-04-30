# Development

## Dependencies

- docker
- java

## Utilities

- httpie
- jq
- pre-commit

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
