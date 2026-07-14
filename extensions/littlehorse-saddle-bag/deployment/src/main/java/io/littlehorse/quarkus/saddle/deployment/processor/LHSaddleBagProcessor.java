package io.littlehorse.quarkus.saddle.deployment.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.ConfigEvaluator.ConfigExpression;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.deployment.item.LHTaskMethodBuildItem;
import io.littlehorse.quarkus.deployment.item.LHTypeAdapterBuildItem;
import io.littlehorse.quarkus.runtime.recordable.LHTypeAdapterRecordable;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.MetadataConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig.Format;
import io.littlehorse.quarkus.saddle.config.LHTaskConfig;
import io.littlehorse.quarkus.saddle.config.LHTaskMethodConfig;
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
import io.littlehorse.sdk.common.adapter.LHTypeAdapterRegistry;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructProperty;
import io.littlehorse.sdk.wfsdk.internal.taskdefutil.LHTaskParameter;
import io.littlehorse.sdk.wfsdk.internal.taskdefutil.LHTaskSignature;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskMethodHandle;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;

import org.eclipse.microprofile.config.ConfigProvider;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LHSaddleBagProcessor {

    private record ResolvedConfig(String name, String configKey) {}

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(
            new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    private static final JavaPropsMapper PROPS_MAPPER = new JavaPropsMapper();

    private static final String JAR_RESOURCE_PATH = "META-INF/saddle-bag/saddle-bag.json";

    @BuildStep
    void generateSaddlebag(
            LHSaddleBagBuildtimeConfig config,
            ApplicationInfoBuildItem applicationInfo,
            List<LHTaskMethodBuildItem> taskMethods,
            List<LHStructDefBuildItem> structDefs,
            List<LHTypeAdapterBuildItem> typeAdapters,
            OutputTargetBuildItem outputTarget,
            BuildProducer<GeneratedResourceBuildItem> resources)
            throws IntrospectionException {
        BagConfig bagConfig = config.saddle().bag();
        OutputConfig outputConfig = bagConfig.output();

        ConfigEvaluator configEvaluator = new ConfigEvaluator(ConfigProvider.getConfig());

        validateMethodConfigs(taskMethods);

        Map<Class<?>, VariableType> typeAdapterMap = buildTypeAdapterMap(typeAdapters);

        SaddleBag saddlebag = buildSaddlebag(
                bagConfig,
                applicationInfo.getVersion(),
                configEvaluator,
                taskMethods,
                structDefs,
                typeAdapterMap);

        generateJarResource(saddlebag, resources);

        if (outputConfig.enable()) {
            generateOutputTargetFile(saddlebag, outputConfig, outputTarget.getOutputDirectory());
        }
    }

    private void generateJarResource(
            SaddleBag saddlebag, BuildProducer<GeneratedResourceBuildItem> resources) {
        byte[] content = serialize(saddlebag, Format.JSON);
        resources.produce(new GeneratedResourceBuildItem(JAR_RESOURCE_PATH, content));
    }

    private void generateOutputTargetFile(
            SaddleBag saddlebag, OutputConfig outputConfig, Path outputDirectory) {
        String extension = outputConfig.format().name().toLowerCase();
        String filename = Path.of(outputConfig.path())
                .resolve(outputConfig.filename() + "." + extension)
                .normalize()
                .toString();
        byte[] content = serialize(saddlebag, outputConfig.format());

        Path outputFile = outputDirectory.resolve(filename).normalize();
        try {
            Files.createDirectories(outputFile.getParent());
            Files.write(outputFile, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate saddlebag file: " + outputFile, e);
        }
    }

    private void validateMethodConfigs(List<LHTaskMethodBuildItem> taskMethods) {
        Map<String, Method> configOwners = new HashMap<>();

        taskMethods.stream()
                .map(item -> item.toRecordable().getBeanClass())
                .distinct()
                .flatMap(beanClass -> Arrays.stream(beanClass.getMethods()))
                .filter(method -> method.isAnnotationPresent(LHTaskMethod.class))
                .forEach(method -> {
                    for (LHTaskMethodConfig annotation :
                            method.getAnnotationsByType(LHTaskMethodConfig.class)) {
                        Method owner = configOwners.putIfAbsent(annotation.value(), method);
                        if (owner != null && !owner.equals(method)) {
                            throw new IllegalStateException("Config '"
                                    + annotation.value()
                                    + "' declared by @LHTaskMethodConfig on "
                                    + owner.getDeclaringClass().getName()
                                    + "#"
                                    + owner.getName()
                                    + " is duplicated in another method "
                                    + method.getDeclaringClass().getName()
                                    + "#"
                                    + method.getName()
                                    + ". Use @LHTaskConfig at the class level to make it a global"
                                    + " saddle bag config.");
                        }
                    }
                });
    }

    private Map<Class<?>, VariableType> buildTypeAdapterMap(
            List<LHTypeAdapterBuildItem> typeAdapters) {
        if (typeAdapters == null || typeAdapters.isEmpty()) {
            return Map.of();
        }

        Map<Class<?>, VariableType> map = new LinkedHashMap<>();
        for (LHTypeAdapterBuildItem item : typeAdapters) {
            LHTypeAdapterRecordable recordable = item.toRecordable();
            map.put(recordable.getAdaptedType(), recordable.getVariableType());
        }
        return map;
    }

    private SaddleBag buildSaddlebag(
            BagConfig bagConfig,
            String version,
            ConfigEvaluator configEvaluator,
            List<LHTaskMethodBuildItem> taskMethods,
            List<LHStructDefBuildItem> structDefs,
            Map<Class<?>, VariableType> typeAdapterMap)
            throws IntrospectionException {

        Map<String, String> placeholderValues =
                buildStructPlaceholderValues(configEvaluator, structDefs);

        return new SaddleBag(
                bagConfig.name(),
                bagConfig.title(),
                bagConfig.author(),
                bagConfig.description(),
                version,
                buildMetadata(bagConfig.metadata()),
                buildSaddleBagTasks(
                        configEvaluator, taskMethods, typeAdapterMap, placeholderValues),
                buildSaddleBagStructs(configEvaluator, structDefs, placeholderValues),
                buildGlobalConfigs(taskMethods));
    }

    private Map<String, String> buildStructPlaceholderValues(
            ConfigEvaluator configEvaluator, List<LHStructDefBuildItem> structDefs) {
        Map<String, String> placeholderValues = new HashMap<>();
        for (LHStructDefBuildItem item : structDefs) {
            placeholderValues.putAll(
                    configEvaluator.expand(item.toRecordable().getName()).getMembers());
        }
        return placeholderValues;
    }

    private Metadata buildMetadata(MetadataConfig metadataConfig) {
        return new Metadata(
                metadataConfig.tags(),
                metadataConfig.licence(),
                metadataConfig.documentationUrl(),
                metadataConfig.iconUrl(),
                metadataConfig.supportEmail());
    }

    private Map<String, Task> buildSaddleBagTasks(
            ConfigEvaluator configEvaluator,
            List<LHTaskMethodBuildItem> taskMethods,
            Map<Class<?>, VariableType> typeAdapterMap,
            Map<String, String> placeholderValues) {
        Map<String, Task> tasks = new LinkedHashMap<>();

        for (LHTaskMethodBuildItem item : taskMethods) {
            ResolvedConfig resolved =
                    resolveConfigExpression(configEvaluator, item.toRecordable().getName());
            Task task = buildSaddleBagTask(
                    item, typeAdapterMap, placeholderValues, resolved.configKey());
            tasks.put(resolved.name(), task);
        }
        return tasks;
    }

    private List<Config> buildGlobalConfigs(List<LHTaskMethodBuildItem> taskMethods) {
        Map<String, Config> configs = new LinkedHashMap<>();

        taskMethods.stream()
                .map(item -> item.toRecordable().getBeanClass())
                .distinct()
                .flatMap(beanClass ->
                        Arrays.stream(beanClass.getAnnotationsByType(LHTaskConfig.class)))
                .forEach(annotation ->
                        configs.putIfAbsent(annotation.value(), toConfig(annotation)));

        return configs.isEmpty() ? null : List.copyOf(configs.values());
    }

    private List<Config> buildMethodConfigs(Method method) {
        Map<String, Config> configs = new LinkedHashMap<>();

        for (LHTaskMethodConfig annotation :
                method.getAnnotationsByType(LHTaskMethodConfig.class)) {
            configs.putIfAbsent(annotation.value(), toConfig(annotation));
        }

        return configs.isEmpty() ? null : List.copyOf(configs.values());
    }

    private Config toConfig(LHTaskConfig annotation) {
        String defaultValue =
                annotation.defaultValue().isEmpty() ? null : annotation.defaultValue();
        return new Config(
                annotation.value(),
                annotation.description(),
                annotation.sensitive(),
                Type.primitive(annotation.type().name()),
                defaultValue);
    }

    private Config toConfig(LHTaskMethodConfig annotation) {
        String defaultValue =
                annotation.defaultValue().isEmpty() ? null : annotation.defaultValue();
        return new Config(
                annotation.value(),
                annotation.description(),
                annotation.sensitive(),
                Type.primitive(annotation.type().name()),
                defaultValue);
    }

    private Task buildSaddleBagTask(
            LHTaskMethodBuildItem taskMethod,
            Map<Class<?>, VariableType> typeAdapterMap,
            Map<String, String> placeholderValues,
            String configKey) {

        String name = taskMethod.toRecordable().getName();

        Output output = null;
        List<Input> inputs = List.of();
        List<TaskException> exceptions = List.of();
        List<Config> configs = null;

        Method[] methods = taskMethod.toRecordable().getBeanClass().getMethods();
        for (Method method : methods) {

            LHTaskMethod annotation = method.getAnnotation(LHTaskMethod.class);
            if (annotation != null && annotation.value().equals(name)) {

                Class<?> returnType = method.getReturnType();
                if (typeAdapterMap.containsKey(returnType)) {
                    output = new Output(primitiveType(typeAdapterMap.get(returnType)));
                } else {
                    LHTaskMethodHandle handle = LHTaskMethodHandle.from(name, "", method);
                    LHTaskSignature signature = new LHTaskSignature(
                            handle, LHTypeAdapterRegistry.empty(), placeholderValues);

                    if (signature.getReturnType().hasReturnType()) {
                        output = new Output(buildType(signature.getReturnType().getReturnType()));
                    }
                }

                inputs = handleTaskParameters(method, typeAdapterMap, placeholderValues);
                exceptions = buildTaskExceptions(method);
                configs = buildMethodConfigs(method);
            }
        }

        return new Task(
                output,
                inputs.isEmpty() ? null : inputs,
                exceptions.isEmpty() ? null : exceptions,
                configKey,
                taskMethod.toRecordable().getDescription(),
                configs);
    }

    private List<TaskException> buildTaskExceptions(Method method) {
        List<TaskException> exceptions = new ArrayList<>();

        LHTaskMethodException[] annotations =
                method.getAnnotationsByType(LHTaskMethodException.class);
        for (LHTaskMethodException annotation : annotations) {
            exceptions.add(new TaskException(annotation.name(), annotation.description()));
        }

        return exceptions;
    }

    private Map<String, Struct> buildSaddleBagStructs(
            ConfigEvaluator configEvaluator,
            List<LHStructDefBuildItem> structDefs,
            Map<String, String> placeholderValues)
            throws IntrospectionException {

        Map<String, Struct> structs = new LinkedHashMap<>();

        for (LHStructDefBuildItem item : structDefs) {
            ResolvedConfig resolved =
                    resolveConfigExpression(configEvaluator, item.toRecordable().getName());
            Struct struct = new Struct(
                    resolved.configKey(),
                    item.toRecordable().getDescription(),
                    buildStruct(item, placeholderValues));
            structs.put(resolved.name(), struct);
        }
        return structs;
    }

    private ResolvedConfig resolveConfigExpression(
            ConfigEvaluator configEvaluator, String rawName) {
        ConfigExpression expression = configEvaluator.expand(rawName);

        if (!expression.isExpression()) {
            throw new IllegalArgumentException(
                    "Name must be a configuration expression (e.g. ${task.my-task.name}), but got: "
                            + rawName);
        }

        if (expression.getMembersCount() != 1) {
            throw new IllegalArgumentException(
                    "Configuration expression must have exactly one member, but got: " + rawName);
        }

        String resolved = expression.asString();
        if (resolved == null || resolved.isBlank()) {
            throw new IllegalArgumentException(
                    "Configuration expression resolved to an empty value: " + rawName);
        }

        String configKey = expression.getMembers().keySet().iterator().next();
        return new ResolvedConfig(resolved, configKey);
    }

    private List<Property> buildStruct(
            LHStructDefBuildItem structDef, Map<String, String> placeholderValues)
            throws IntrospectionException {

        LHStructDefType structDefType = new LHStructDefType(
                structDef.toRecordable().getBeanClass(),
                LHTypeAdapterRegistry.empty(),
                placeholderValues);
        List<LHStructProperty> properties = structDefType.getStructProperties();

        List<Property> structProperties = new ArrayList<>();

        for (LHStructProperty property : properties) {
            structProperties.add(new Property(
                    property.getFieldName(),
                    buildType(property.getPropertyType().getTypeDefinition())));
        }

        return structProperties;
    }

    private List<Input> handleTaskParameters(
            Method method,
            Map<Class<?>, VariableType> typeAdapterMap,
            Map<String, String> placeholderValues) {

        List<Input> parameters = new ArrayList<>();

        LHTaskMethodHandle handle = LHTaskMethodHandle.from(
                method.getAnnotation(LHTaskMethod.class).value(), "", method);
        LHTaskSignature signature =
                new LHTaskSignature(handle, LHTypeAdapterRegistry.empty(), placeholderValues);

        java.lang.reflect.Parameter[] methodParams = method.getParameters();
        List<LHTaskParameter> taskParams = signature.getVariableDefs();

        for (int i = 0; i < taskParams.size(); i++) {
            LHTaskParameter lhTaskParameter = taskParams.get(i);
            Class<?> paramType = methodParams[i].getType();

            Type type;
            if (typeAdapterMap.containsKey(paramType)) {
                type = primitiveType(typeAdapterMap.get(paramType));
            } else {
                type = buildType(lhTaskParameter.getVariableDef().getTypeDef());
            }

            parameters.add(new Input(lhTaskParameter.getVariableName(), type));
        }
        return parameters;
    }

    private Type buildType(TypeDefinition typeDef) {
        return switch (typeDef.getDefinedTypeCase()) {
            case STRUCT_DEF_ID -> Type.struct(typeDef.getStructDefId().getName());
            case INLINE_ARRAY_DEF ->
                Type.array(buildType(typeDef.getInlineArrayDef().getArrayType()));
            case INLINE_MAP_DEF ->
                Type.map(
                        buildType(typeDef.getInlineMapDef().getKeyType()),
                        buildType(typeDef.getInlineMapDef().getValueType()));
            default -> Type.primitive(typeDef.getPrimitiveType().name());
        };
    }

    private Type primitiveType(VariableType variableType) {
        return Type.primitive(variableType.name());
    }

    byte[] serialize(SaddleBag data, Format format) {
        try {
            ObjectMapper mapper =
                    switch (format) {
                        case JSON -> JSON_MAPPER;
                        case YAML -> YAML_MAPPER;
                        case PROPERTIES -> PROPS_MAPPER;
                    };
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(new IOException("Failed to serialize saddlebag", e));
        }
    }

    SaddleBag deserialize(byte[] content, Format format) {
        try {
            ObjectMapper mapper =
                    switch (format) {
                        case JSON -> JSON_MAPPER;
                        case YAML -> YAML_MAPPER;
                        case PROPERTIES -> PROPS_MAPPER;
                    };
            return mapper.readValue(content, SaddleBag.class);
        } catch (IOException e) {
            throw new UncheckedIOException(new IOException("Failed to deserialize saddlebag", e));
        }
    }
}
