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
./gradlew dockerComposeUp
```

Stop LH:

```shell
./gradlew dockerComposeDown
```

| Container             | Port |
| --------------------- | ---- |
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

> The LH version is defined in `gradle.properties` (`version` property) and passed automatically to docker-compose via the Gradle tasks.

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

### Get taskWorkers:

```shell
http :8080/gateway/tenants/default/task-defs/restful-gateway-demo-task/workers
```

Equivalent to:

```shell
lhctl get taskWorkerGroup restful-gateway-demo-task
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

### Local Development with Keycloak

The project includes a Keycloak instance in the `docker-compose.yml` for local development.

Start it with:

```shell
docker compose up keycloak -d
```

Keycloak is available at `http://localhost:8888` with admin credentials `admin`/`admin`.

A pre-configured realm `lh-gateway` is automatically imported with:

| Resource       | Value                |
|----------------|----------------------|
| Client ID      | `lh-gateway`         |
| Client Secret  | `lh-gateway-secret`  |
| Realm          | `lh-gateway`         |

**Users:**

| Username | Password | Roles                            |
|----------|----------|----------------------------------|
| admin    | admin    | gateway-admin, gateway-reader    |
| reader   | reader   | gateway-reader                   |

**Obtaining a token:**

```shell
curl -X POST http://localhost:8888/realms/lh-gateway/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=lh-gateway" \
  -d "client_secret=lh-gateway-secret" \
  -d "username=admin" \
  -d "password=admin"
```

### Example: Enabling RBAC Mode

This example shows how to configure the gateway with RBAC mode using Keycloak as the Identity Provider.

**1. Add the required dependencies:**

Gradle:

```groovy
implementation "io.quarkus:quarkus-oidc"
implementation project(":littlehorse-quarkus")
implementation project(":littlehorse-quarkus-restful-gateway")
```

Maven:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-oidc</artifactId>
</dependency>
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus</artifactId>
    <version>${lhVersion}</version>
</dependency>
<dependency>
    <groupId>io.littlehorse</groupId>
    <artifactId>littlehorse-quarkus-restful-gateway</artifactId>
    <version>${lhVersion}</version>
</dependency>
```

**2. Configure `application.properties`:**

```properties
# LittleHorse connection
lhc.api.host=localhost
lhc.api.port=2023

# OAuth 2 RBAC mode
quarkus.littlehorse.gateway.oauth2.mode=RBAC

# OIDC configuration (Keycloak)
quarkus.oidc.enabled=true
quarkus.oidc.auth-server-url=http://localhost:8888/realms/lh-gateway
quarkus.oidc.client-id=lh-gateway
quarkus.oidc.credentials.secret=lh-gateway-secret
quarkus.http.auth.proactive=true

# Role names (must match the roles configured in your IdP)
quarkus.littlehorse.gateway.oauth2.rbac.admin-role=gateway-admin
quarkus.littlehorse.gateway.oauth2.rbac.reader-role=gateway-reader
```

**3. Start the infrastructure:**

```shell
docker compose up -d
```

**4. Run the gateway application:**

```shell
./gradlew :restful-gateway:quarkusDev
```

**5. Obtain a token and make requests:**

```shell
# Get a token for the admin user (read + write access)
TOKEN=$(curl -s -X POST http://localhost:8888/realms/lh-gateway/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=lh-gateway" \
  -d "client_secret=lh-gateway-secret" \
  -d "username=admin" \
  -d "password=admin" | jq -r '.access_token')

# Read operation (allowed for both admin and reader roles)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/gateway/tenants/default/wf-specs

# Write operation (allowed only for admin role)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"wfSpecName": "greetings", "variables": {"name": "World"}}' \
  http://localhost:8080/gateway/tenants/default/wf-runs
```

```shell
# Get a token for the reader user (read-only access)
TOKEN=$(curl -s -X POST http://localhost:8888/realms/lh-gateway/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=lh-gateway" \
  -d "client_secret=lh-gateway-secret" \
  -d "username=reader" \
  -d "password=reader" | jq -r '.access_token')

# Read operation (allowed)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/gateway/tenants/default/wf-specs

# Write operation (denied with 403 Forbidden)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"wfSpecName": "greetings", "variables": {"name": "World"}}' \
  http://localhost:8080/gateway/tenants/default/wf-runs
```
## Links

- [Writing a Quarkus Extensions](https://quarkus.io/guides/writing-extensions)
- [Quarkus Extension Metadata](https://quarkus.io/guides/extension-metadata)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config-reference)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi-reference)
- [Writing Native Application Tips](https://quarkus.io/guides/writing-native-applications-tips)
