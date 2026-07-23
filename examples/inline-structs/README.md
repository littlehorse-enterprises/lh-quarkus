# Inline Structs Example

This example shows how to use inline structs whose `StructDef` names are supplied through
Quarkus configuration. The `Customer` class uses a placeholder in `@LHStructDef`:

Raw `InlineStruct` values are intended for advanced cases where task code needs to work with the
protobuf value directly. When the Java type is known, prefer the typed approach shown in the
[Structs example](../structs).

```java
@LHStructDef("${customer.struct.name}")
public class Customer {
    // fields, constructors, getters, and setters
}
```

The placeholder is configured in `application.properties`:

```properties
customer.struct.name=customer-acme
```

The LittleHorse Quarkus extension resolves the placeholder and registers the resulting
`customer-acme` `StructDef`. The workflow and task signatures use the same placeholder value. The
workflow runs two tasks: `create-customer` returns a raw `InlineStruct`, and `email-customer`
receives it. The task return and parameter bind the raw value to the registered StructDef with
`@LHType(structDefName = "${customer.struct.name}")`.

```java
@LHTaskMethod("create-customer")
@LHType(structDefName = "${customer.struct.name}")
public InlineStruct createCustomer(String name, String email) {
    return InlineStruct.newBuilder()
            // Add fields compatible with the Customer StructDef.
            .build();
}

@LHTaskMethod("email-customer")
public String emailCustomer(
        @LHType(structDefName = "${customer.struct.name}") InlineStruct customer,
        String message) {
    String email = customer.getFieldsOrThrow("email").getValue().getStr();
    return "Sent to " + email;
}
```

The extension carries the placeholder value from the scanned `@LHStructDef` into task
registration and runtime input/output serialization. The same placeholder therefore needs to be
used by the StructDef and the `@LHType` annotations.

## Running the Example

Start LittleHorse:

```shell
./gradlew dockerComposeUp
```

Run the example:

```shell
./gradlew example-inline-structs:quarkusDev
```

Start a workflow run:

```shell
lhctl run inline-structs name Leia email leia@rebellion.example message 'Welcome to LittleHorse'
```

Inspect workflow runs:

```shell
lhctl search wfRun inline-structs
```
