# Type Adapter Example

This example shows you how to use type adapters.

Annotate your type adapter class with `@LHTypeAdapter`, specifying the target `VariableType` and the adapted Java class.

```java
@LHTypeAdapter(variableType = VariableType.STR, adaptedType = UUID.class)
public class UUIDTypeAdapter implements LHStringAdapter<UUID> {

    @Override
    public String toString(UUID src) {
        return src.toString();
    }

    @Override
    public UUID fromString(String src) {
        return UUID.fromString(src);
    }

    @Override
    public Class<UUID> getTypeClass() {
        return UUID.class;
    }
}
```

## Running the Example

Setup:

```shell
docker compose up -d
```

Execute it:

```shell
./gradlew example-type-adapter:quarkusDev
```

Run workflow:

```shell
lhctl run example-type-adapter
```

Check workflows:

```shell
lhctl search wfRun example-type-adapter
```
