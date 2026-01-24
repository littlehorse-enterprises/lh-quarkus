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

## RESTful Gateway

Execute it:

```shell
./gradlew restful-gateway:quarkusDev
```

> Swagger UI at http://localhost:8080/q/swagger-ui/

### Get server version:

```shell
http :8080/gateway/version
```

Equivalent to:

```shell
lhctl version
```

### Get wfSpec:

```shell
http :8080/gateway/tenants/default/wf-specs/restful-gateway-demo-workflow
http :8080/gateway/tenants/default/wf-specs/restful-gateway-demo-workflow/versions/0.0
```

Equivalent to:

```shell
lhctl get wfSpec restful-gateway-demo-workflow
lhctl get wfSpec restful-gateway-demo-workflow --majorVersion 0 --revision 0
```

### Search wfSpec:

```shell
http :8080/gateway/tenants/default/wf-specs
http :8080/gateway/tenants/default/wf-specs limit==1 prefix==restful-gateway
```

Equivalent to:

```shell
lhctl search wfSpec
lhctl search wfSpec --limit 1 --prefix restful-gateway-demo-workflow
```

### Get taskDef:

```shell
http :8080/gateway/tenants/default/task-defs/restful-gateway-demo-task
```

Equivalent to:

```shell
lhctl get taskDef restful-gateway-demo-task
```

### Search taskDef:

```shell
http :8080/gateway/tenants/default/task-defs
http :8080/gateway/tenants/default/task-defs limit==1 prefix==restful-gateway
```

Equivalent to:

```shell
lhctl search taskDef
lhctl search taskDef --limit 1 restful-gateway-demo-task
```

### Run wf:

```shell
http POST :8080/gateway/tenants/default/wf-runs wfSpecName=restful-gateway-demo-workflow 'variables[name]'=Anakin id=my-workflow-id
```

Equivalent to:

```shell
lhctl run restful-gateway-demo-workflow
```

### Get wfRun:

```shell
http :8080/gateway/tenants/default/wf-runs/my-workflow-id
```

Equivalent to:

```shell
lhctl get wfRun my-workflow-id
```

### Get variables:

```shell
http :8080/gateway/tenants/default/wf-runs/my-workflow-id/variables
```

Equivalent to:

```shell
lhctl list variable my-workflow-id
```

### External events:

Run wf:

```shell
http POST :8080/gateway/tenants/default/wf-runs wfSpecName=restful-gateway-external-event-demo-workflow id=my-external-event-id
```

Gent wf (should be running):

```shell
http :8080/gateway/tenants/default/wf-runs/my-external-event-id
```

Post event:

```shell
http POST :8080/gateway/tenants/default/external-events externalEventDefName=restful-gateway-external-event-demo wfRunId=my-external-event-id
```

> Then the wf run should be completed.

Equivalent to:

```shell
lhctl postEvent my-external-event-id restful-gateway-external-event-demo
```

### OAuth

Pass the token in the http request ([Bearer auth](https://httpie.io/docs/cli/bearer-auth)):

```shell
http -A bearer -a $TOKEN :8080/gateway/tenants/default/wf-specs/greetings
```

## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Extension Metadata](https://quarkus.io/guides/extension-metadata)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
- [Writing Native Application Tips](https://quarkus.io/guides/writing-native-applications-tips)
