# Project Guidelines

## Overview

Quarkus extensions for the [LittleHorse](https://littlehorse.io/) workflow engine. Provides CDI integration, automatic task/workflow registration, a RESTful gateway for the LittleHorse gRPC API, and self-describing task worker packaging via Saddle Bag.

## Architecture

- `extensions/littlehorse-quarkus/` — Core extension (runtime + deployment modules)
- `extensions/littlehorse-restful-gateway/` — RESTful gateway extension (runtime + deployment)
- `extensions/littlehorse-saddle-bag/` — Saddle Bag extension for self-describing task worker Docker images (runtime + deployment)
- `extras/restful-gateway/` — Standalone Quarkus app using the gateway extension
- `examples/` — Usage examples (arrays-maps, basic, child-workflow, inline-structs, reactive, rest, saddle-bag, structs, type-adapter, user-tasks)
- `integration-tests/` — End-to-end tests against a running LittleHorse instance, split per concern into subprojects named after the extension they cover: `littlehorse-quarkus/` (project `integration-tests-littlehorse-quarkus`), `littlehorse-restful-gateway/` (project `integration-tests-littlehorse-restful-gateway`), `littlehorse-saddle-bag/` (project `integration-tests-littlehorse-saddle-bag`)

Each extension follows the Quarkus extension structure: `deployment/` for build-time processors, `runtime/` for CDI beans and recorders.

## Build and Test

```shell
# Build
./gradlew build

# Unit tests
./gradlew test

# Integration tests (requires Docker; the tests start LittleHorse with Testcontainers)
./gradlew integration-tests-littlehorse-quarkus:quarkusIntTest integration-tests-littlehorse-restful-gateway:quarkusIntTest integration-tests-littlehorse-saddle-bag:quarkusIntTest

# Native integration tests
./gradlew integration-tests-littlehorse-quarkus:quarkusIntTest integration-tests-littlehorse-restful-gateway:quarkusIntTest integration-tests-littlehorse-saddle-bag:quarkusIntTest -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false

# Publish locally (uses the version from gradle.properties)
./gradlew publishToMavenLocal

# Run RESTful Gateway in dev mode
./gradlew restful-gateway:quarkusDev
```

## Code Style

- Java 17
- Palantir Java Format (AOSP style) enforced via Spotless
- Run `./gradlew spotlessApply` to auto-format before committing
- Pre-commit and commit-message hooks are configured — run `pre-commit install` after cloning

## Commit Messages

- Every commit must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification.
- Use the format `<type>(<optional scope>): <description>` (for example, `feat(examples): add inline structs example`).
- Use an appropriate standard type: `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `revert`, `style`, or `test`.
- Mark breaking changes with `!` before the colon or with a `BREAKING CHANGE:` footer.

## Conventions

- Quarkus version: `3.37.2`, LittleHorse SDK: `1.2-SNAPSHOT`
- Dependency versions are centralized in `gradle.properties`
- Extension projects: `littlehorse-quarkus`, `littlehorse-restful-gateway`, `littlehorse-saddle-bag` (each also has a `-deployment` project)
- Example projects use the `example-` prefix: `example-arrays-maps`, `example-basic`, `example-child-workflow`, `example-inline-structs`, `example-reactive`, `example-rest`, `example-saddle-bag`, `example-structs`, `example-type-adapter`, `example-user-tasks`
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
- See [extensions/littlehorse-restful-gateway/README.md](extensions/littlehorse-restful-gateway/README.md) for gateway usage
- See [extensions/littlehorse-saddle-bag/README.md](extensions/littlehorse-saddle-bag/README.md) for Saddle Bag usage
