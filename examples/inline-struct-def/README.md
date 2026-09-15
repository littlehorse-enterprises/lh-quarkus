# Inline StructDef Example

This example embeds unannotated Java beans directly in workflow and task type definitions. It does
not register a standalone `StructDef`.

`Customer` contains a nested, also unannotated, `DeliveryAddress`. The workflow declares the
embedded schema with `declareInlineStruct`:

```java
WfRunVariable customer =
        wf.declareInlineStruct("customer", Customer.class).required();
```

Task inputs and outputs opt into the same anonymous schema with
`@LHType(isInlineStruct = true)`:

```java
@LHTaskMethod("normalize-customer")
@LHType(isInlineStruct = true)
public Customer normalizeCustomer(
        @LHType(isInlineStruct = true) Customer customer) {
    return customer;
}
```

The LittleHorse Java SDK recursively includes `DeliveryAddress` in the embedded schema and
serializes worker values as Structs without a `StructDefId`.

## Running the Example

Start LittleHorse:

```shell
./gradlew dockerComposeUp
```

Run the example:

```shell
./gradlew example-inline-struct-def:quarkusDev
```

Start a workflow run with a JSON value matching the embedded schema:

```shell
lhctl run inline-struct-def \
  customer '{"name":"Leia","email":"leia@rebellion.example","address":{"street":" Royal Avenue ","city":" Alderaan "}}' \
  message 'Welcome to LittleHorse'
```

Inspect workflow runs:

```shell
lhctl search wfRun inline-struct-def
```