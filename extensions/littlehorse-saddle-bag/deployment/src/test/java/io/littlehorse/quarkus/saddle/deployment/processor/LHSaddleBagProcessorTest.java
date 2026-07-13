package io.littlehorse.quarkus.saddle.deployment.processor;

/*
 * Standalone helper to read the generated properties file and print it as JSON:
 *
 * package io.littlehorse.quarkus.saddle.deployment;
 *
 * import com.fasterxml.jackson.databind.ObjectMapper;
 * import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
 *
 * import java.nio.file.Files;
 * import java.nio.file.Path;
 * import java.util.Map;
 *
 * public class PropertiesToJsonConverter {
 *
 *     public static void main(String[] args) throws Exception {
 *         Path propertiesFile =
 *                 Path.of("examples/saddle-bag/build/saddle-bag/saddle-bag.properties");
 *
 *         JavaPropsMapper propsMapper = new JavaPropsMapper();
 *         String propertiesContent = Files.readString(propertiesFile);
 *         Map<?, ?> data = propsMapper.readValue(propertiesContent, Map.class);
 *
 *         ObjectMapper jsonMapper = new ObjectMapper();
 *         String json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
 *
 *         System.out.println(json);
 *     }
 * }
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;
import io.littlehorse.quarkus.deployment.descriptor.LHStructDefDescriptor;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig.Format;
import io.littlehorse.quarkus.saddle.config.LHTaskConfig.LHTaskConfigType;
import io.littlehorse.sdk.common.adapter.LHTypeAdapterRegistry;
import io.littlehorse.sdk.common.proto.InlineArrayDef;
import io.littlehorse.sdk.common.proto.InlineMapDef;
import io.littlehorse.sdk.common.proto.StructDefId;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.wfsdk.internal.taskdefutil.LHTaskSignature;
import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskMethodHandle;
import io.littlehorse.sdk.worker.LHType;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class LHSaddleBagProcessorTest {

    private final LHSaddleBagProcessor processor = new LHSaddleBagProcessor();

    @Test
    void shouldProduceSameContentAcrossAllFormats() {
        Map<String, Object> saddlebag = sampleSaddlebag();

        Map<String, Object> fromJson = roundTrip(saddlebag, Format.JSON);
        Map<String, Object> fromYaml = roundTrip(saddlebag, Format.YAML);
        Map<String, Object> fromProperties = roundTrip(saddlebag, Format.PROPERTIES);

        assertThat(normalize(fromJson))
                .isEqualTo(normalize(fromYaml))
                .isEqualTo(normalize(fromProperties))
                .isEqualTo(normalize(saddlebag));
    }

    @Test
    void putTypeInfoEmitsPrimitiveType() {
        Map<String, Object> target = new LinkedHashMap<>();

        processor.putTypeInfo(
                target,
                TypeDefinition.newBuilder().setPrimitiveType(VariableType.STR).build());

        assertThat(target).containsExactly(entry("type", "STR"));
    }

    @Test
    void putTypeInfoEmitsStructType() {
        Map<String, Object> target = new LinkedHashMap<>();

        processor.putTypeInfo(
                target,
                TypeDefinition.newBuilder()
                        .setStructDefId(StructDefId.newBuilder().setName("test-order"))
                        .build());

        assertThat(target).containsExactly(entry("type", "STRUCT"), entry("struct", "test-order"));
    }

    @Test
    void handleTaskParametersDistinguishesStructsFromPrimitives() throws Exception {
        Method method =
                TestOrderTask.class.getMethod("createOrder", String.class, TestAddress.class);

        List<Map<String, Object>> params =
                processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(2);
        assertThat(params.get(0)).containsEntry("type", "STR").doesNotContainKey("struct");
        assertThat(params.get(1))
                .containsEntry("type", "STRUCT")
                .containsEntry("struct", "test-address");
    }

    @Test
    void detectsStructReturnType() throws Exception {
        Method method =
                TestOrderTask.class.getMethod("createOrder", String.class, TestAddress.class);
        LHTaskSignature signature = new LHTaskSignature(
                LHTaskMethodHandle.from("create-order", "", method),
                LHTypeAdapterRegistry.empty(),
                Map.of());

        Map<String, Object> output = new LinkedHashMap<>();
        processor.putTypeInfo(output, signature.getReturnType().getReturnType());

        assertThat(output).containsExactly(entry("type", "STRUCT"), entry("struct", "test-order"));
    }

    @Test
    void detectsPrimitiveReturnType() throws Exception {
        Method method = TestOrderTask.class.getMethod("addNumbers", int.class, int.class);
        LHTaskSignature signature = new LHTaskSignature(
                LHTaskMethodHandle.from("add-numbers", "", method),
                LHTypeAdapterRegistry.empty(),
                Map.of());

        Map<String, Object> output = new LinkedHashMap<>();
        processor.putTypeInfo(output, signature.getReturnType().getReturnType());

        assertThat(output).containsExactly(entry("type", "INT"));
    }

    @Test
    void buildStructEmitsNestedStructAndPrimitiveProperties() throws Exception {
        LHStructDefBuildItem item = new LHStructDefBuildItem(
                TestOrder.class, new LHStructDefDescriptor(new OptionalAnnotation(null)));

        List<Map<String, Object>> properties = processor.buildStruct(item, Map.of());

        assertThat(findProperty(properties, "productName")).containsEntry("type", "STR");
        assertThat(findProperty(properties, "shippingAddress"))
                .containsEntry("type", "STRUCT")
                .containsEntry("struct", "test-address");
    }

    @Test
    void resolvesStructNamePlaceholdersInTaskParameters() throws Exception {
        Method method = TestPlaceholderTask.class.getMethod("handle", TestPlaceholderAddress.class);

        List<Map<String, Object>> params = processor.handleTaskParameters(
                method, Map.of(), Map.of("struct.ph-address.name", "resolved-address"));

        assertThat(params).hasSize(1);
        assertThat(params.get(0))
                .containsEntry("type", "STRUCT")
                .containsEntry("struct", "resolved-address");
    }

    @Test
    void putTypeInfoEmitsArrayType() {
        Map<String, Object> target = new LinkedHashMap<>();

        processor.putTypeInfo(
                target,
                TypeDefinition.newBuilder()
                        .setInlineArrayDef(InlineArrayDef.newBuilder()
                                .setArrayType(TypeDefinition.newBuilder()
                                        .setPrimitiveType(VariableType.STR)))
                        .build());

        assertThat(target)
                .containsExactly(entry("type", "ARRAY"), entry("element", Map.of("type", "STR")));
    }

    @Test
    void putTypeInfoEmitsMapType() {
        Map<String, Object> target = new LinkedHashMap<>();

        processor.putTypeInfo(
                target,
                TypeDefinition.newBuilder()
                        .setInlineMapDef(InlineMapDef.newBuilder()
                                .setKeyType(TypeDefinition.newBuilder()
                                        .setPrimitiveType(VariableType.STR))
                                .setValueType(TypeDefinition.newBuilder()
                                        .setPrimitiveType(VariableType.INT)))
                        .build());

        assertThat(target)
                .containsExactly(
                        entry("type", "MAP"),
                        entry("key", Map.of("type", "STR")),
                        entry("value", Map.of("type", "INT")));
    }

    @Test
    void handleTaskParametersEmitsArrayOfPrimitives() throws Exception {
        Method method = TestCollectionTask.class.getMethod("consumeArray", Long[].class);

        List<Map<String, Object>> params =
                processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(1);
        assertThat(params.get(0))
                .containsEntry("type", "ARRAY")
                .containsEntry("element", Map.of("type", "INT"));
    }

    @Test
    void handleTaskParametersEmitsArrayOfStructs() throws Exception {
        Method method = TestCollectionTask.class.getMethod("consumeAddresses", TestAddress[].class);

        List<Map<String, Object>> params =
                processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(1);
        assertThat(params.get(0))
                .containsEntry("type", "ARRAY")
                .containsEntry("element", Map.of("type", "STRUCT", "struct", "test-address"));
    }

    @Test
    void handleTaskParametersEmitsMapType() throws Exception {
        Method method = TestCollectionTask.class.getMethod("consumeMap", Map.class);

        List<Map<String, Object>> params =
                processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(1);
        assertThat(params.get(0))
                .containsEntry("type", "MAP")
                .containsEntry("key", Map.of("type", "STR"))
                .containsEntry("value", Map.of("type", "INT"));
    }

    @Test
    void detectsArrayReturnType() throws Exception {
        Method method = TestCollectionTask.class.getMethod("produceArray");
        LHTaskSignature signature = new LHTaskSignature(
                LHTaskMethodHandle.from("produce-array", "", method),
                LHTypeAdapterRegistry.empty(),
                Map.of());

        Map<String, Object> output = new LinkedHashMap<>();
        processor.putTypeInfo(output, signature.getReturnType().getReturnType());

        assertThat(output)
                .containsExactly(entry("type", "ARRAY"), entry("element", Map.of("type", "INT")));
    }

    @Test
    void buildStructEmitsArrayAndMapProperties() throws Exception {
        LHStructDefBuildItem item = new LHStructDefBuildItem(
                TestInventory.class, new LHStructDefDescriptor(new OptionalAnnotation(null)));

        List<Map<String, Object>> properties = processor.buildStruct(item, Map.of());

        assertThat(findProperty(properties, "tags"))
                .containsEntry("type", "ARRAY")
                .containsEntry("element", Map.of("type", "STR"));
        assertThat(findProperty(properties, "counts"))
                .containsEntry("type", "MAP")
                .containsEntry("key", Map.of("type", "STR"))
                .containsEntry("value", Map.of("type", "INT"));
    }

    private Map<String, Object> roundTrip(Map<String, Object> saddlebag, Format format) {
        byte[] serialized = processor.serialize(saddlebag, format);
        System.out.println("Serialized " + format + ":\n" + new String(serialized) + "\n");
        return processor.deserialize(serialized, format);
    }

    private Map<String, Object> sampleSaddlebag() {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("name", "example-saddle-bag");
        root.put("title", "Example Saddle Bag");
        root.put("author", "LittleHorse");
        root.put("description", "An example saddle bag");
        root.put("version", "1.2-SNAPSHOT");

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("tags", List.of("test", "example"));
        metadata.put("licence", "MIT");
        metadata.put("documentation-url", "https://example.com/docs");
        root.put("metadata", metadata);

        Map<String, Object> tasks = new LinkedHashMap<>();

        Map<String, Object> addNumbers = new LinkedHashMap<>();
        addNumbers.put("output", Map.of("type", "INT"));
        addNumbers.put(
                "inputs",
                List.of(Map.of("name", "a", "type", "INT"), Map.of("name", "b", "type", "INT")));
        addNumbers.put("config-name", "task.add-numbers.name");
        addNumbers.put("description", "Adds two integers and returns their sum");
        tasks.put("add-numbers", addNumbers);

        Map<String, Object> notification = new LinkedHashMap<>();
        notification.put("output", Map.of("type", "STR"));
        notification.put("config-name", "task.send-notification.name");
        notification.put("description", "Sends a notification");

        List<Map<String, Object>> configs = new ArrayList<>();
        Map<String, Object> urlConfig = new LinkedHashMap<>();
        urlConfig.put("key", "notification.service.url");
        urlConfig.put("description", "Notification service base URL");
        urlConfig.put("sensitive", false);
        urlConfig.put("type", LHTaskConfigType.STR);
        configs.add(urlConfig);
        Map<String, Object> apiKeyConfig = new LinkedHashMap<>();
        apiKeyConfig.put("key", "notification.service.api-key");
        apiKeyConfig.put("description", "API key for the notification service");
        apiKeyConfig.put("sensitive", true);
        apiKeyConfig.put("default-value", "5000");
        apiKeyConfig.put("type", LHTaskConfigType.STR);
        configs.add(apiKeyConfig);
        notification.put("configs", configs);
        tasks.put("send-notification", notification);

        root.put("tasks", tasks);

        Map<String, Object> structs = new LinkedHashMap<>();
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("config-name", "struct.order.name");
        order.put("description", "Represents a customer order");
        order.put(
                "properties",
                List.of(
                        Map.of("name", "price", "type", "DOUBLE"),
                        Map.of("name", "quantity", "type", "INT")));
        structs.put("order", order);
        root.put("structs", structs);

        return root;
    }

    /**
     * Normalizes a deserialized structure so the contents can be compared across formats. The
     * properties format is untyped (booleans and numbers become strings) and represents lists as
     * index-keyed maps, so scalars are coerced to strings and numeric-keyed maps are turned into
     * lists.
     */
    private Object normalize(Object value) {
        if (value instanceof Map<?, ?> map) {
            if (!map.isEmpty() && map.keySet().stream().allMatch(this::isInteger)) {
                List<Object> list = new ArrayList<>();
                Map<Integer, Object> byIndex = new TreeMap<>();
                map.forEach((key, element) ->
                        byIndex.put(Integer.parseInt(String.valueOf(key)), element));
                byIndex.values().forEach(element -> list.add(normalize(element)));
                return list;
            }
            Map<String, Object> normalized = new LinkedHashMap<>();
            map.forEach((key, element) -> normalized.put(String.valueOf(key), normalize(element)));
            return normalized;
        }
        if (value instanceof List<?> list) {
            List<Object> normalized = new ArrayList<>();
            list.forEach(element -> normalized.add(normalize(element)));
            return normalized;
        }
        return String.valueOf(value);
    }

    private boolean isInteger(Object key) {
        if (!(key instanceof String string)) {
            return false;
        }
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Map<String, Object> findProperty(List<Map<String, Object>> properties, String name) {
        return properties.stream()
                .filter(property -> name.equals(property.get("name")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Property not found: " + name));
    }

    @LHStructDef("test-address")
    public static class TestAddress {
        private String street;
        private int zipCode;

        public TestAddress() {}

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public int getZipCode() {
            return zipCode;
        }

        public void setZipCode(int zipCode) {
            this.zipCode = zipCode;
        }
    }

    @LHStructDef("test-order")
    public static class TestOrder {
        private String productName;
        private TestAddress shippingAddress;

        public TestOrder() {}

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public TestAddress getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(TestAddress shippingAddress) {
            this.shippingAddress = shippingAddress;
        }
    }

    public static class TestOrderTask {

        @LHTaskMethod("create-order")
        public TestOrder createOrder(String productName, TestAddress address) {
            return new TestOrder();
        }

        @LHTaskMethod("add-numbers")
        public int addNumbers(int a, int b) {
            return a + b;
        }
    }

    @LHStructDef("${struct.ph-address.name}")
    public static class TestPlaceholderAddress {
        private String street;

        public TestPlaceholderAddress() {}

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    public static class TestPlaceholderTask {

        @LHTaskMethod("ph-task")
        public void handle(TestPlaceholderAddress address) {}
    }

    public static class TestCollectionTask {

        @LHTaskMethod("consume-array")
        public void consumeArray(@LHType(isLHArray = true) Long[] numbers) {}

        @LHTaskMethod("consume-addresses")
        public void consumeAddresses(@LHType(isLHArray = true) TestAddress[] addresses) {}

        @LHTaskMethod("consume-map")
        public void consumeMap(@LHType(isLHMap = true) Map<String, Long> counts) {}

        @LHTaskMethod("produce-array")
        @LHType(isLHArray = true)
        public Long[] produceArray() {
            return new Long[0];
        }
    }

    @LHStructDef("test-inventory")
    public static class TestInventory {
        private String[] tags;
        private Map<String, Long> counts;

        public TestInventory() {}

        public String[] getTags() {
            return tags;
        }

        public void setTags(String[] tags) {
            this.tags = tags;
        }

        public Map<String, Long> getCounts() {
            return counts;
        }

        public void setCounts(Map<String, Long> counts) {
            this.counts = counts;
        }
    }
}
