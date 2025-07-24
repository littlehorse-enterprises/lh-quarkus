# Rest Example

This example shows you how to use LH in a REST endpoint on quarkus.

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-rest:quarkusDev
```

Send a request without id:

```shell
http :8080/hello name=="Anakin"
```

Send a request with id:

```shell
http :8080/hello name=="Leia" id==my-id-1
```

Check workflows:

```shell
lhctl search wfRun greetings
```
