# Project Guidelines

## Overview

Quarkus extensions for the [LittleHorse](https://littlehorse.io/) workflow engine. Provides CDI integration, automatic task/workflow registration, a RESTful gateway for the LittleHorse gRPC API, and self-describing task worker packaging via Saddle Bag.

## Architecture

- `extensions/littlehorse-quarkus/` — Core extension (runtime + deployment modules)
- `extensions/littlehorse-quarkus-restful-gateway/` — RESTful gateway extension (runtime + deployment)
- `extensions/littlehorse-saddle-bag/` — Saddle Bag extension for self-describing task worker Docker images (runtime + deployment)
- `restful-gateway/` — Standalone Quarkus app using the gateway extension
- `examples/` — Usage examples (basic, child-workflow, reactive, rest, saddle-bag, structs, type-adapter, user-tasks)
- `integration-tests/` — End-to-end tests against a running LittleHorse instance

Each extension follows the Quarkus extension structure: `deployment/` for build-time processors, `runtime/` for CDI beans and recorders.

## Build and Test

```shell
# Build
./gradlew build

# Unit tests
./gradlew test

# Integration tests (requires ./gradlew dockerComposeUp)
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

- Quarkus version: `3.35.1`, LittleHorse SDK: `1.2-SNAPSHOT`
- Dependency versions are centralized in `gradle.properties`
- Extension projects: `quarkus`, `quarkus-restful-gateway`, `saddle-bag`
- Example projects: `user-tasks`, `reactive`, `rest`, `basic`, `child-workflow`, `structs`, `type-adapter`, `saddle-bag`
- Use `@LHTaskMethod` and related annotations for task/workflow registration
- Use `@LHTaskConfig` to declare required external configurations for Saddle Bag manifests
- Deployment processors scan annotations at build time — runtime beans are produced via recorders

## Development Environment

```shell
./gradlew dockerComposeUp
```

Starts LittleHorse (port 2023), Kafka (9092), and LH Dashboard (3000). Versions are sourced from `gradle.properties`.

To stop:

```shell
./gradlew dockerComposeDown
```

## References

- See [DEVELOPMENT.md](DEVELOPMENT.md) for full setup details
- See [extensions/littlehorse-quarkus/README.md](extensions/littlehorse-quarkus/README.md) for extension usage
- See [extensions/littlehorse-quarkus-restful-gateway/README.md](extensions/littlehorse-quarkus-restful-gateway/README.md) for gateway usage
- See [extensions/littlehorse-saddle-bag/README.md](extensions/littlehorse-saddle-bag/README.md) for Saddle Bag usage
