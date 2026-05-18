# Project Guidelines

## Overview

Quarkus extensions for the [LittleHorse](https://littlehorse.io/) workflow engine. Provides CDI integration, automatic task/workflow registration, and a RESTful gateway for the LittleHorse gRPC API.

## Architecture

- `extensions/littlehorse-quarkus/` — Core extension (runtime + deployment modules)
- `extensions/littlehorse-quarkus-restful-gateway/` — RESTful gateway extension (runtime + deployment)
- `restful-gateway/` — Standalone Quarkus app using the gateway extension
- `examples/` — Usage examples (basic, child-workflow, reactive, rest, structs, type-adapter, user-tasks)
- `integration-tests/` — End-to-end tests against a running LittleHorse instance

Each extension follows the Quarkus extension structure: `deployment/` for build-time processors, `runtime/` for CDI beans and recorders.

## Build and Test

```shell
# Build
./gradlew build

# Unit tests
./gradlew test

# Integration tests (requires docker compose up -d)
./gradlew integration-tests:quarkusIntTest

# Native integration tests
./gradlew integration-tests:quarkusIntTest -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false

# Publish locally (version=dev)
./gradlew publishToMavenLocal

# Run RESTful Gateway in dev mode
./gradlew restful-gateway:quarkusDev
```

## Code Style

- Java 17
- Palantir Java Format (AOSP style) enforced via Spotless
- Run `./gradlew spotlessApply` to auto-format before committing
- Pre-commit hooks are configured — run `pre-commit install` after cloning

## Conventions

- Quarkus version: `3.32.3`, LittleHorse SDK: `1.1.0-SNAPSHOT`
- Dependency versions are centralized in `gradle.properties`
- Extension projects: `quarkus`, `quarkus-restful-gateway`
- Example projects: `user-tasks`, `reactive`, `rest`, `basic`, `child-workflow`, `structs`, `type-adapter`
- Use `@LHTaskMethod` and related annotations for task/workflow registration
- Deployment processors scan annotations at build time — runtime beans are produced via recorders

## Development Environment

```shell
docker compose up -d
```

Starts LittleHorse (port 2023), Kafka (9092), and LH Dashboard (3000).

## References

- See [DEVELOPMENT.md](DEVELOPMENT.md) for full setup details
- See [extensions/littlehorse-quarkus/README.md](extensions/littlehorse-quarkus/README.md) for extension usage
- See [extensions/littlehorse-quarkus-restful-gateway/README.md](extensions/littlehorse-quarkus-restful-gateway/README.md) for gateway usage
