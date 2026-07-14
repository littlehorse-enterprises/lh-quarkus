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

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;
import io.littlehorse.quarkus.deployment.descriptor.LHStructDefDescriptor;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig.Format;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Config;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Input;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Metadata;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Output;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Property;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Struct;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Task;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Type;
import io.littlehorse.quarkus.saddle.exception.LHTaskMethodException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class LHSaddleBagProcessorTest {

    private final LHSaddleBagProcessor processor = new LHSaddleBagProcessor();

    @Test
    void shouldProduceSameContentAcrossAllFormats() {
        SaddleBag saddlebag = sampleSaddlebag();

        SaddleBag fromJson = roundTrip(saddlebag, Format.JSON);
        SaddleBag fromYaml = roundTrip(saddlebag, Format.YAML);
        SaddleBag fromProperties = roundTrip(saddlebag, Format.PROPERTIES);

        assertThat(fromJson).isEqualTo(saddlebag);
        assertThat(fromYaml).isEqualTo(saddlebag);
        assertThat(fromProperties).isEqualTo(saddlebag);
    }

    @Test
    void buildTypeEmitsPrimitiveType() {
        Type type = processor.buildType(
                TypeDefinition.newBuilder().setPrimitiveType(VariableType.STR).build());

        assertThat(type).isEqualTo(Type.primitive("STR"));
    }

    @Test
    void buildTypeEmitsStructType() {
        Type type = processor.buildType(TypeDefinition.newBuilder()
                .setStructDefId(StructDefId.newBuilder().setName("test-order"))
                .build());

        assertThat(type).isEqualTo(Type.struct("test-order"));
    }

    @Test
    void handleTaskParametersDistinguishesStructsFromPrimitives() throws Exception {
        Method method =
                TestOrderTask.class.getMethod("createOrder", String.class, TestAddress.class);

        List<Input> params = processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(2);
        assertThat(params.get(0).type()).isEqualTo(Type.primitive("STR"));
        assertThat(params.get(1).type()).isEqualTo(Type.struct("test-address"));
    }

    @Test
    void detectsStructReturnType() throws Exception {
        Method method =
                TestOrderTask.class.getMethod("createOrder", String.class, TestAddress.class);
        LHTaskSignature signature = new LHTaskSignature(
                LHTaskMethodHandle.from("create-order", "", method),
                LHTypeAdapterRegistry.empty(),
                Map.of());

        Type type = processor.buildType(signature.getReturnType().getReturnType());

        assertThat(type).isEqualTo(Type.struct("test-order"));
    }

    @Test
    void detectsPrimitiveReturnType() throws Exception {
        Method method = TestOrderTask.class.getMethod("addNumbers", int.class, int.class);
        LHTaskSignature signature = new LHTaskSignature(
                LHTaskMethodHandle.from("add-numbers", "", method),
                LHTypeAdapterRegistry.empty(),
                Map.of());

        Type type = processor.buildType(signature.getReturnType().getReturnType());

        assertThat(type).isEqualTo(Type.primitive("INT"));
    }

    @Test
    void buildStructEmitsNestedStructAndPrimitiveProperties() throws Exception {
        LHStructDefBuildItem item = new LHStructDefBuildItem(
                TestOrder.class, new LHStructDefDescriptor(new OptionalAnnotation(null)));

        List<Property> properties = processor.buildStruct(item, Map.of());

        assertThat(findProperty(properties, "productName").type()).isEqualTo(Type.primitive("STR"));
        assertThat(findProperty(properties, "shippingAddress").type())
                .isEqualTo(Type.struct("test-address"));
    }

    @Test
    void resolvesStructNamePlaceholdersInTaskParameters() throws Exception {
        Method method = TestPlaceholderTask.class.getMethod("handle", TestPlaceholderAddress.class);

        List<Input> params = processor.handleTaskParameters(
                method, Map.of(), Map.of("struct.ph-address.name", "resolved-address"));

        assertThat(params).hasSize(1);
        assertThat(params.get(0).type()).isEqualTo(Type.struct("resolved-address"));
    }

    @Test
    void buildTypeEmitsArrayType() {
        Type type = processor.buildType(TypeDefinition.newBuilder()
                .setInlineArrayDef(InlineArrayDef.newBuilder()
                        .setArrayType(
                                TypeDefinition.newBuilder().setPrimitiveType(VariableType.STR)))
                .build());

        assertThat(type).isEqualTo(Type.array(Type.primitive("STR")));
    }

    @Test
    void buildTypeEmitsMapType() {
        Type type = processor.buildType(TypeDefinition.newBuilder()
                .setInlineMapDef(InlineMapDef.newBuilder()
                        .setKeyType(TypeDefinition.newBuilder().setPrimitiveType(VariableType.STR))
                        .setValueType(
                                TypeDefinition.newBuilder().setPrimitiveType(VariableType.INT)))
                .build());

        assertThat(type).isEqualTo(Type.map(Type.primitive("STR"), Type.primitive("INT")));
    }

    @Test
    void handleTaskParametersEmitsArrayOfPrimitives() throws Exception {
        Method method = TestCollectionTask.class.getMethod("consumeArray", Long[].class);

        List<Input> params = processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(1);
        assertThat(params.get(0).type()).isEqualTo(Type.array(Type.primitive("INT")));
    }

    @Test
    void handleTaskParametersEmitsArrayOfStructs() throws Exception {
        Method method = TestCollectionTask.class.getMethod("consumeAddresses", TestAddress[].class);

        List<Input> params = processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(1);
        assertThat(params.get(0).type()).isEqualTo(Type.array(Type.struct("test-address")));
    }

    @Test
    void handleTaskParametersEmitsMapType() throws Exception {
        Method method = TestCollectionTask.class.getMethod("consumeMap", Map.class);

        List<Input> params = processor.handleTaskParameters(method, Map.of(), Map.of());

        assertThat(params).hasSize(1);
        assertThat(params.get(0).type())
                .isEqualTo(Type.map(Type.primitive("STR"), Type.primitive("INT")));
    }

    @Test
    void detectsArrayReturnType() throws Exception {
        Method method = TestCollectionTask.class.getMethod("produceArray");
        LHTaskSignature signature = new LHTaskSignature(
                LHTaskMethodHandle.from("produce-array", "", method),
                LHTypeAdapterRegistry.empty(),
                Map.of());

        Type type = processor.buildType(signature.getReturnType().getReturnType());

        assertThat(type).isEqualTo(Type.array(Type.primitive("INT")));
    }

    @Test
    void buildTaskExceptionsEmitsAnnotatedBusinessExceptions() throws Exception {
        Method method = TestExceptionTask.class.getMethod("charge", double.class);

        List<SaddleBag.TaskException> exceptions = processor.buildTaskExceptions(method);

        assertThat(exceptions).hasSize(2);
        assertThat(exceptions.get(0).name()).isEqualTo("insufficient-funds");
        assertThat(exceptions.get(0).description()).isEqualTo("Card balance too low");
        assertThat(exceptions.get(1).name()).isEqualTo("amount-too-large");
        assertThat(exceptions.get(1).description()).isEqualTo("");
    }

    @Test
    void buildTaskExceptionsEmitsEmptyListWhenNotAnnotated() throws Exception {
        Method method = TestOrderTask.class.getMethod("addNumbers", int.class, int.class);

        assertThat(processor.buildTaskExceptions(method)).isEmpty();
    }

    @Test
    void buildStructEmitsArrayAndMapProperties() throws Exception {
        LHStructDefBuildItem item = new LHStructDefBuildItem(
                TestInventory.class, new LHStructDefDescriptor(new OptionalAnnotation(null)));

        List<Property> properties = processor.buildStruct(item, Map.of());

        assertThat(findProperty(properties, "tags").type())
                .isEqualTo(Type.array(Type.primitive("STR")));
        assertThat(findProperty(properties, "counts").type())
                .isEqualTo(Type.map(Type.primitive("STR"), Type.primitive("INT")));
    }

    private SaddleBag roundTrip(SaddleBag saddlebag, Format format) {
        byte[] serialized = processor.serialize(saddlebag, format);
        System.out.println("Serialized " + format + ":\n" + new String(serialized) + "\n");
        return processor.deserialize(serialized, format);
    }

    private SaddleBag sampleSaddlebag() {
        Metadata metadata = new Metadata(
                List.of("test", "example"), "MIT", "https://example.com/docs", null, null);

        Task addNumbers = new Task(
                new Output(Type.primitive("INT")),
                List.of(
                        new Input("a", Type.primitive("INT")),
                        new Input("b", Type.primitive("INT"))),
                null,
                "task.add-numbers.name",
                "Adds two integers and returns their sum",
                null);

        Task notification = new Task(
                new Output(Type.primitive("STR")),
                null,
                null,
                "task.send-notification.name",
                "Sends a notification",
                List.of(
                        new Config(
                                "notification.service.url",
                                "Notification service base URL",
                                false,
                                Type.primitive("STR"),
                                null),
                        new Config(
                                "notification.service.api-key",
                                "API key for the notification service",
                                true,
                                Type.primitive("STR"),
                                "5000")));

        Map<String, Task> tasks = new LinkedHashMap<>();
        tasks.put("add-numbers", addNumbers);
        tasks.put("send-notification", notification);

        Struct order = new Struct(
                "struct.order.name",
                "Represents a customer order",
                List.of(
                        new Property("price", Type.primitive("DOUBLE")),
                        new Property("quantity", Type.primitive("INT"))));

        Map<String, Struct> structs = new LinkedHashMap<>();
        structs.put("order", order);

        return new SaddleBag(
                "example-saddle-bag",
                "Example Saddle Bag",
                "LittleHorse",
                "An example saddle bag",
                "1.2-SNAPSHOT",
                metadata,
                tasks,
                structs);
    }

    private Property findProperty(List<Property> properties, String name) {
        return properties.stream()
                .filter(property -> name.equals(property.name()))
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

    public static class TestExceptionTask {

        @LHTaskMethod("charge")
        @LHTaskMethodException(name = "insufficient-funds", description = "Card balance too low")
        @LHTaskMethodException(name = "amount-too-large")
        public void charge(double amount) {}
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
