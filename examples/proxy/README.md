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
http :8080/proxy/server
```
