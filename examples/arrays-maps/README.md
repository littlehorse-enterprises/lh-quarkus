# Arrays & Maps Example

Demonstrates LittleHorse **native `Array` and `Map` types** in a Quarkus task worker.

Array and Map support is inherited directly from the LittleHorse Java SDK, so there is nothing
extra to configure in the extension:

- Task parameters and return types are annotated with `@LHType(isLHArray = true)` /
  `@LHType(isLHMap = true)`.
- Workflow variables are declared with `wf.declareArray(name, elementType)` and
  `wf.declareMap(name, keyType, valueType)`.

The `arrays-maps` workflow:

1. Declares an `Array<INT>` variable `numbers` (default `[1, 2, 3]`) and a `Map<STR, INT>` variable
   `counts` (default `{"apples": 3, "bananas": 5}`).
2. Appends the array produced by `produce-array` using `EXTEND` and sums it via `sum-array`.
3. Consumes the map via `count-map`.

## Running

Start the dev environment from the repository root:

```shell
./gradlew dockerComposeUp
```

Run the example:

```shell
./gradlew example-arrays-maps:quarkusDev
```

Trigger a run with `lhctl` (uses the default variable values):

```shell
lhctl run arrays-maps
```

Or override the inputs:

```shell
lhctl run arrays-maps numbers '[10, 20, 30]' counts '{"apples": 10, "grapes": 7}'
```

Inspect the run:

```shell
lhctl get wfRun <wf_run_id>
```
