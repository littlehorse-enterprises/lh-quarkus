# Rest Proxy Example

This example shows you how to use LH REST Proxy extension.

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-proxy:quarkusDev
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
