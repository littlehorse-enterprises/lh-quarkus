package io.littlehorse.quarkus.saddle.deployment.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;
import io.littlehorse.quarkus.deployment.descriptor.LHStructDefDescriptor;
import io.littlehorse.quarkus.deployment.descriptor.LHTaskMethodDescriptor;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.deployment.item.LHTaskMethodBuildItem;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig.Format;
import io.littlehorse.quarkus.saddle.config.LHTaskConfig;
import io.littlehorse.quarkus.saddle.config.LHTaskConfig.LHTaskConfigType;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Config;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Input;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Metadata;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Output;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Property;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Struct;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Task;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.TaskException;
import io.littlehorse.quarkus.saddle.deployment.model.SaddleBag.Type;
import io.littlehorse.quarkus.saddle.exception.LHTaskMethodException;
import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

class LHSaddleBagProcessorTest {

    private final LHSaddleBagProcessor processor = new LHSaddleBagProcessor();

    // ---------------------------------------------------------------------------------------------
    // Serialization round-trip (serialize/deserialize are the only non-private helpers besides the
    // generateSaddlebag build step).
    // ---------------------------------------------------------------------------------------------

    @Test
    void shouldProduceSameContentAcrossAllFormats() {
        SaddleBag saddlebag = sampleSaddlebag();

        assertThat(roundTrip(saddlebag, Format.JSON)).isEqualTo(saddlebag);
        assertThat(roundTrip(saddlebag, Format.YAML)).isEqualTo(saddlebag);
        assertThat(roundTrip(saddlebag, Format.PROPERTIES)).isEqualTo(saddlebag);
    }

    // ---------------------------------------------------------------------------------------------
    // Type descriptors (inputs / outputs / struct properties) driven end-to-end through the build
    // step and asserted on the generated output file.
    // ---------------------------------------------------------------------------------------------

    @Test
    void generateSaddlebagEmitsPrimitiveInputAndOutputTypes() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(PrimitivesTask.class)), Map.of("task.add.name", "add"));

        Task add = saddlebag.tasks().get("add");
        assertThat(add.output().type()).isEqualTo(Type.primitive("INT"));
        assertThat(add.inputs())
                .extracting(Input::type)
                .containsExactly(Type.primitive("INT"), Type.primitive("INT"));
    }

    @Test
    void generateSaddlebagEmitsStructInputAndOutputTypes() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(StructTask.class)),
                Map.of("task.create-address.name", "create-address"));

        Task task = saddlebag.tasks().get("create-address");
        assertThat(task.output().type()).isEqualTo(Type.struct("address"));
        assertThat(task.inputs())
                .extracting(Input::type)
                .containsExactly(Type.primitive("STR"), Type.struct("address"));
    }

    @Test
    void generateSaddlebagEmitsArrayAndMapTypes() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(
                        taskItem(CollectionTask.class, "consumeArray"),
                        taskItem(CollectionTask.class, "consumeAddresses"),
                        taskItem(CollectionTask.class, "consumeMap"),
                        taskItem(CollectionTask.class, "produceArray")),
                Map.of(
                        "task.consume-array.name", "consume-array",
                        "task.consume-addresses.name", "consume-addresses",
                        "task.consume-map.name", "consume-map",
                        "task.produce-array.name", "produce-array"));

        assertThat(saddlebag.tasks().get("consume-array").inputs().get(0).type())
                .isEqualTo(Type.array(Type.primitive("INT")));
        assertThat(saddlebag.tasks().get("consume-addresses").inputs().get(0).type())
                .isEqualTo(Type.array(Type.struct("address")));
        assertThat(saddlebag.tasks().get("consume-map").inputs().get(0).type())
                .isEqualTo(Type.map(Type.primitive("STR"), Type.primitive("INT")));
        assertThat(saddlebag.tasks().get("produce-array").output().type())
                .isEqualTo(Type.array(Type.primitive("INT")));
    }

    @Test
    void generateSaddlebagResolvesStructNamePlaceholderInTaskInput() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(PlaceholderTask.class)),
                List.of(structItem(PlaceholderAddress.class)),
                Map.of("task.ph.name", "ph", "struct.ph-address.name", "resolved-address"));

        assertThat(saddlebag.tasks().get("ph").inputs().get(0).type())
                .isEqualTo(Type.struct("resolved-address"));
    }

    @Test
    void generateSaddlebagEmitsStructPropertyTypes() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(),
                List.of(structItem(OrderStruct.class), structItem(InventoryStruct.class)),
                Map.of("struct.order.name", "order", "struct.inventory.name", "inventory"));

        Struct order = saddlebag.structs().get("order");
        assertThat(propertyType(order, "productName")).isEqualTo(Type.primitive("STR"));
        assertThat(propertyType(order, "quantity")).isEqualTo(Type.primitive("INT"));
        assertThat(propertyType(order, "shippingAddress")).isEqualTo(Type.struct("address"));

        Struct inventory = saddlebag.structs().get("inventory");
        assertThat(propertyType(inventory, "tags")).isEqualTo(Type.array(Type.primitive("STR")));
        assertThat(propertyType(inventory, "counts"))
                .isEqualTo(Type.map(Type.primitive("STR"), Type.primitive("INT")));
    }

    // ---------------------------------------------------------------------------------------------
    // Task business exceptions.
    // ---------------------------------------------------------------------------------------------

    @Test
    void generateSaddlebagEmitsAnnotatedBusinessExceptions() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(ExceptionTask.class)), Map.of("task.charge.name", "charge"));

        Task charge = saddlebag.tasks().get("charge");
        assertThat(charge.exceptions())
                .containsExactly(
                        new TaskException("insufficient-funds", "Card balance too low"),
                        new TaskException("amount-too-large", ""));
    }

    @Test
    void generateSaddlebagOmitsExceptionsWhenNoneDeclared() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(NoConfigTask.class)), Map.of("task.plain.name", "plain"));

        assertThat(saddlebag.tasks().get("plain").exceptions()).isNull();
    }

    // ---------------------------------------------------------------------------------------------
    // Global (class-level @LHTaskConfig) and method-level (@LHTaskConfig) configurations.
    // ---------------------------------------------------------------------------------------------

    @Test
    void generateSaddlebagEmitsGlobalConfigsAtRootDeduplicatedAcrossClasses() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(GlobalConfigTaskA.class), taskItem(GlobalConfigTaskB.class)),
                Map.of("task.alpha.name", "alpha", "task.beta.name", "beta"));

        assertThat(saddlebag.configs())
                .extracting(Config::key)
                .containsExactly("shared.url", "task-a.key", "task-b.key");
        assertThat(saddlebag.configs().get(0))
                .isEqualTo(
                        new Config("shared.url", "Shared URL", false, Type.primitive("STR"), null));
    }

    @Test
    void generateSaddlebagEmitsMethodConfigsInsideOwningTaskDeduplicated() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(GlobalConfigTaskA.class), taskItem(GlobalConfigTaskB.class)),
                Map.of("task.alpha.name", "alpha", "task.beta.name", "beta"));

        assertThat(saddlebag.tasks().get("alpha").configs())
                .containsExactly(new Config(
                        "alpha.retries", "Alpha retries", false, Type.primitive("INT"), "3"));
        assertThat(saddlebag.tasks().get("beta").configs())
                .containsExactly(new Config(
                        "beta.timeout", "Beta timeout", false, Type.primitive("INT"), null));
    }

    @Test
    void generateSaddlebagOmitsConfigsWhenNoneDeclared() throws Exception {
        SaddleBag saddlebag = generateSaddlebag(
                List.of(taskItem(NoConfigTask.class)), Map.of("task.plain.name", "plain"));

        assertThat(saddlebag.configs()).isNull();
        assertThat(saddlebag.tasks().get("plain").configs()).isNull();
    }

    @Test
    void generateSaddlebagFailsWhenSameMethodConfigKeyOnTwoMethods() {
        assertThatThrownBy(() -> generateSaddlebag(
                        List.of(taskItem(DuplicateAcrossMethodsTask.class, "first")),
                        Map.of("task.first.name", "first", "task.second.name", "second")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dup.key")
                .hasMessageContaining("@LHTaskConfig");
    }

    @Test
    void generateSaddlebagFailsWhenTasksAreDuplicated() {
        assertThatThrownBy(() -> generateSaddlebag(
                        List.of(taskItem(NoConfigTask.class), taskItem(NoConfigTask.class)),
                        Map.of("task.plain.name", "plain")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicated tasks are not allowed")
                .hasMessageContaining("plain");
    }

    @Test
    void generateSaddlebagFailsWhenStructsAreDuplicated() {
        assertThatThrownBy(() -> generateSaddlebag(
                        List.of(),
                        List.of(
                                structItem(PlaceholderAddress.class),
                                structItem(PlaceholderAddress.class)),
                        Map.of("struct.ph-address.name", "resolved-address")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicated structs are not allowed")
                .hasMessageContaining("resolved-address");
    }

    // ---------------------------------------------------------------------------------------------
    // Test harness: drives the generateSaddlebag build step and reads back the generated JSON file.
    // ---------------------------------------------------------------------------------------------

    private SaddleBag generateSaddlebag(
            List<LHTaskMethodBuildItem> taskMethods, Map<String, String> nameProperties)
            throws Exception {
        return generateSaddlebag(taskMethods, List.of(), nameProperties);
    }

    private SaddleBag generateSaddlebag(
            List<LHTaskMethodBuildItem> taskMethods,
            List<LHStructDefBuildItem> structDefs,
            Map<String, String> nameProperties)
            throws Exception {
        Path outputDir = Files.createTempDirectory("saddlebag-test");

        Map<String, String> properties = new HashMap<>(bagProperties());
        properties.putAll(nameProperties);

        SmallRyeConfig config = new SmallRyeConfigBuilder()
                .withMapping(LHSaddleBagBuildtimeConfig.class)
                .withValidateUnknown(false)
                .withSources(new PropertiesConfigSource(properties, "test", 250))
                .build();

        ConfigProviderResolver resolver = ConfigProviderResolver.instance();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            resolver.registerConfig(config, classLoader);
        } catch (IllegalStateException alreadyRegistered) {
            resolver.releaseConfig(resolver.getConfig(classLoader));
            resolver.registerConfig(config, classLoader);
        }

        try {
            LHSaddleBagBuildtimeConfig mapping =
                    config.getConfigMapping(LHSaddleBagBuildtimeConfig.class);
            ApplicationInfoBuildItem applicationInfo =
                    new ApplicationInfoBuildItem(Optional.of("test-app"), Optional.of("1.0.0"));
            OutputTargetBuildItem outputTarget = new OutputTargetBuildItem(
                    outputDir, "test-app", "test-app", false, new Properties(), Optional.empty());
            BuildProducer<GeneratedResourceBuildItem> resources =
                    new ArrayList<GeneratedResourceBuildItem>()::add;

            processor.generateSaddlebag(
                    mapping,
                    applicationInfo,
                    taskMethods,
                    structDefs,
                    List.of(),
                    outputTarget,
                    resources);

            Path outputFile = outputDir.resolve("saddle-bag/saddle-bag.json");
            return processor.deserialize(Files.readAllBytes(outputFile), Format.JSON);
        } finally {
            resolver.releaseConfig(config);
        }
    }

    private Map<String, String> bagProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("quarkus.littlehorse.saddle.bag.name", "test-bag");
        properties.put("quarkus.littlehorse.saddle.bag.title", "Test Bag");
        properties.put("quarkus.littlehorse.saddle.bag.author", "tester");
        properties.put("quarkus.littlehorse.saddle.bag.description", "A test bag");
        properties.put("quarkus.littlehorse.saddle.bag.metadata.tags", "t1,t2");
        properties.put("quarkus.littlehorse.saddle.bag.metadata.licence", "MIT");
        properties.put(
                "quarkus.littlehorse.saddle.bag.metadata.documentation-url",
                "https://example.com/docs");
        properties.put(
                "quarkus.littlehorse.saddle.bag.metadata.icon-url", "https://example.com/icon.png");
        properties.put(
                "quarkus.littlehorse.saddle.bag.metadata.support-email", "support@example.com");
        properties.put(
                "quarkus.littlehorse.saddle.bag.metadata.docker-image",
                "ghcr.io/example-org/order-service");
        properties.put("quarkus.littlehorse.saddle.bag.output.enable", "true");
        properties.put("quarkus.littlehorse.saddle.bag.output.format", "json");
        properties.put("quarkus.littlehorse.saddle.bag.output.path", "saddle-bag/");
        properties.put("quarkus.littlehorse.saddle.bag.output.filename", "saddle-bag");
        return properties;
    }

    private LHTaskMethodBuildItem taskItem(Class<?> beanClass) {
        String taskName = Arrays.stream(beanClass.getMethods())
                .filter(method -> method.isAnnotationPresent(LHTaskMethod.class))
                .map(method -> method.getAnnotation(LHTaskMethod.class).value())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No @LHTaskMethod on " + beanClass));
        return taskItem(beanClass, taskName);
    }

    private LHTaskMethodBuildItem taskItem(Class<?> beanClass, String methodName) {
        String taskName = Arrays.stream(beanClass.getMethods())
                .filter(method -> method.isAnnotationPresent(LHTaskMethod.class))
                .filter(method -> method.getName().equals(methodName)
                        || method.getAnnotation(LHTaskMethod.class).value().equals(methodName))
                .map(method -> method.getAnnotation(LHTaskMethod.class).value())
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("No task method " + methodName + " on " + beanClass));

        return new LHTaskMethodBuildItem(
                beanClass,
                new LHTaskMethodDescriptor(annotation(LHTaskMethod.class.getName(), taskName)));
    }

    private LHStructDefBuildItem structItem(Class<?> beanClass) {
        String structName = beanClass.getAnnotation(LHStructDef.class).value();
        return new LHStructDefBuildItem(
                beanClass,
                new LHStructDefDescriptor(annotation(LHStructDef.class.getName(), structName)));
    }

    private OptionalAnnotation annotation(String annotationType, String value) {
        AnnotationInstance instance = AnnotationInstance.create(
                DotName.createSimple(annotationType),
                null,
                new AnnotationValue[] {AnnotationValue.createStringValue("value", value)});
        return new OptionalAnnotation(instance);
    }

    private Type propertyType(Struct struct, String propertyName) {
        return struct.properties().stream()
                .filter(property -> propertyName.equals(property.name()))
                .map(Property::type)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Property not found: " + propertyName));
    }

    private SaddleBag roundTrip(SaddleBag saddlebag, Format format) {
        byte[] serialized = processor.serialize(saddlebag, format);
        return processor.deserialize(serialized, format);
    }

    private SaddleBag sampleSaddlebag() {
        Metadata metadata = new Metadata(
                List.of("test", "example"),
                "MIT",
                "https://example.com/docs",
                null,
                null,
                "ghcr.io/example-org/order-service");

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
                List.of(new Config(
                        "notification.channel",
                        "Notification channel",
                        false,
                        Type.primitive("STR"),
                        null)));

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

        List<Config> configs = List.of(
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
                        "5000"));

        return new SaddleBag(
                "example-saddle-bag",
                "Example Saddle Bag",
                "LittleHorse",
                "An example saddle bag",
                "1.2-SNAPSHOT",
                metadata,
                tasks,
                structs,
                configs);
    }

    // ---------------------------------------------------------------------------------------------
    // Fixtures. Task names and top-level struct names use configuration expressions, mirroring real
    // usage; struct types referenced as inputs/outputs/nested properties use literal names.
    // ---------------------------------------------------------------------------------------------

    @LHStructDef("address")
    public static class Address {
        private String street;
        private int zipCode;

        public Address() {}

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

    @LHStructDef("${struct.order.name}")
    public static class OrderStruct {
        private String productName;
        private int quantity;
        private Address shippingAddress;

        public OrderStruct() {}

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Address getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(Address shippingAddress) {
            this.shippingAddress = shippingAddress;
        }
    }

    @LHStructDef("${struct.inventory.name}")
    public static class InventoryStruct {
        private String[] tags;
        private Map<String, Long> counts;

        public InventoryStruct() {}

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

    @LHStructDef("${struct.ph-address.name}")
    public static class PlaceholderAddress {
        private String street;

        public PlaceholderAddress() {}

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    public static class PrimitivesTask {

        @LHTaskMethod("${task.add.name}")
        public int add(int a, int b) {
            return a + b;
        }
    }

    public static class StructTask {

        @LHTaskMethod("${task.create-address.name}")
        public Address createAddress(String label, Address existing) {
            return existing;
        }
    }

    public static class CollectionTask {

        @LHTaskMethod("${task.consume-array.name}")
        public void consumeArray(@LHType(isLHArray = true) Long[] numbers) {}

        @LHTaskMethod("${task.consume-addresses.name}")
        public void consumeAddresses(@LHType(isLHArray = true) Address[] addresses) {}

        @LHTaskMethod("${task.consume-map.name}")
        public void consumeMap(@LHType(isLHMap = true) Map<String, Long> counts) {}

        @LHTaskMethod("${task.produce-array.name}")
        @LHType(isLHArray = true)
        public Long[] produceArray() {
            return new Long[0];
        }
    }

    public static class PlaceholderTask {

        @LHTaskMethod("${task.ph.name}")
        public void handle(PlaceholderAddress address) {}
    }

    public static class ExceptionTask {

        @LHTaskMethod("${task.charge.name}")
        @LHTaskMethodException(name = "insufficient-funds", description = "Card balance too low")
        @LHTaskMethodException(name = "amount-too-large")
        public void charge(double amount) {}
    }

    public static class NoConfigTask {

        @LHTaskMethod("${task.plain.name}")
        public void plain() {}
    }

    @LHTaskConfig(value = "shared.url", description = "Shared URL", type = LHTaskConfigType.STR)
    @LHTaskConfig(value = "task-a.key", description = "Task A key", type = LHTaskConfigType.INT)
    public static class GlobalConfigTaskA {

        @LHTaskMethod("${task.alpha.name}")
        @LHTaskConfig(
                value = "alpha.retries",
                description = "Alpha retries",
                defaultValue = "3",
                type = LHTaskConfigType.INT)
        public void alpha() {}
    }

    @LHTaskConfig(value = "shared.url", description = "Shared URL", type = LHTaskConfigType.STR)
    @LHTaskConfig(value = "task-b.key", description = "Task B key", type = LHTaskConfigType.BOOL)
    public static class GlobalConfigTaskB {

        @LHTaskMethod("${task.beta.name}")
        @LHTaskConfig(
                value = "beta.timeout",
                description = "Beta timeout",
                type = LHTaskConfigType.INT)
        @LHTaskConfig(
                value = "beta.timeout",
                description = "Duplicate ignored",
                type = LHTaskConfigType.INT)
        public void beta() {}
    }

    public static class DuplicateAcrossMethodsTask {

        @LHTaskMethod("${task.first.name}")
        @LHTaskConfig(value = "dup.key", type = LHTaskConfigType.STR)
        public void first() {}

        @LHTaskMethod("${task.second.name}")
        @LHTaskConfig(value = "dup.key", type = LHTaskConfigType.STR)
        public void second() {}
    }
}
