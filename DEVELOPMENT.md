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
docker run --name littlehorse -d -p 2023:2023 -p 8080:8080 \
ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:latest
```

## Tests

```shell
./gradlew test
```

## Publish Locally

```shell
./gradlew publishToMavenLocal
```

> Optionally you can pas ` -Pversion=<version>`

## Run

```shell
./gradlew quarkusDev
http -v :9000/hello name==Luck id==my-workflow-1
http -v :9000/hello/reactive name==Luck id==my-workflow-2
```

## Run Native

```shell
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
./example/build/example*runner
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
