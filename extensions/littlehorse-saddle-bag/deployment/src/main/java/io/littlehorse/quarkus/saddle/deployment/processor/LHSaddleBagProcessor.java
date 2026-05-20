package io.littlehorse.quarkus.saddle.deployment.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.ConfigEvaluator.ConfigExpression;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.deployment.item.LHTaskMethodBuildItem;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.MetadataConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig;
import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig.Format;
import io.littlehorse.sdk.common.adapter.LHTypeAdapterRegistry;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LHSaddleBagProcessor {

    private record ResolvedConfig(String name, String configKey) {}

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(
            new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

    private static final String JAR_RESOURCE_PATH = "META-INF/saddle-bag/saddle-bag.json";

    @BuildStep
    void generateSaddlebag(
            LHSaddleBagBuildtimeConfig config,
            ApplicationInfoBuildItem applicationInfo,
            List<LHTaskMethodBuildItem> taskMethods,
            List<LHStructDefBuildItem> structDefs,
            OutputTargetBuildItem outputTarget,
            BuildProducer<GeneratedResourceBuildItem> resources)
            throws IntrospectionException {
        BagConfig bagConfig = config.saddle().bag();
        OutputConfig outputConfig = bagConfig.output();

        ConfigEvaluator configEvaluator = new ConfigEvaluator(ConfigProvider.getConfig());

        Map<String, Object> saddlebag = buildSaddlebag(
                bagConfig, applicationInfo.getVersion(), configEvaluator, taskMethods, structDefs);

        generateJarResource(saddlebag, resources);

        if (outputConfig.enable()) {
            generateOutputTargetFile(saddlebag, outputConfig, outputTarget.getOutputDirectory());
        }
    }

    private void generateJarResource(
            Map<String, Object> saddlebag, BuildProducer<GeneratedResourceBuildItem> resources) {
        byte[] content = serialize(saddlebag, Format.JSON);
        resources.produce(new GeneratedResourceBuildItem(JAR_RESOURCE_PATH, content));
    }

    private void generateOutputTargetFile(
            Map<String, Object> saddlebag, OutputConfig outputConfig, Path outputDirectory) {
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

    private Map<String, Object> buildSaddlebag(
            BagConfig bagConfig,
            String version,
            ConfigEvaluator configEvaluator,
            List<LHTaskMethodBuildItem> taskMethods,
            List<LHStructDefBuildItem> structDefs)
            throws IntrospectionException {

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("name", bagConfig.name());
        root.put("title", bagConfig.title());
        root.put("description", bagConfig.description());
        root.put("version", version);
        root.put("metadata", buildMetadata(bagConfig.metadata()));
        root.put("tasks", buildSaddleBagTasks(configEvaluator, taskMethods));
        root.put("structs", buildSaddleBagStructs(configEvaluator, structDefs));
        return root;
    }

    private Map<String, Object> buildMetadata(MetadataConfig metadataConfig) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("tags", metadataConfig.tags());
        map.put("licence", metadataConfig.licence());
        map.put("documentation-url", metadataConfig.documentationUrl());
        map.put("icon-url", metadataConfig.iconUrl());
        map.put("support-email", metadataConfig.supportEmail());
        return map;
    }

    private Map<String, Object> buildSaddleBagTasks(
            ConfigEvaluator configEvaluator, List<LHTaskMethodBuildItem> taskMethods) {
        Map<String, Object> tasks = new LinkedHashMap<>();

        for (LHTaskMethodBuildItem item : taskMethods) {
            ResolvedConfig resolved =
                    resolveConfigExpression(configEvaluator, item.toRecordable().getName());
            Map<String, Object> task = buildSaddleBagTask(item);
            task.put("configName", resolved.configKey());
            task.put("description", item.toRecordable().getDescription());
            tasks.put(resolved.name(), task);
        }
        return tasks;
    }

    private Map<String, Object> buildSaddleBagTask(LHTaskMethodBuildItem taskMethod) {

        Map<String, Object> task = new LinkedHashMap<>();

        String name = taskMethod.toRecordable().getName();

        Method[] methods = taskMethod.toRecordable().getBeanClass().getMethods();
        for (Method method : methods) {

            LHTaskMethod annotation = method.getAnnotation(LHTaskMethod.class);
            if (annotation != null && annotation.value().equals(name)) {

                LHTaskMethodHandle handle = LHTaskMethodHandle.from(name, "", method);

                LHTaskSignature signature =
                        new LHTaskSignature(handle, LHTypeAdapterRegistry.empty(), Map.of());

                if (signature.getReturnType().hasReturnType()) {
                    task.put(
                            "output",
                            Map.of(
                                    "type",
                                    signature
                                            .getReturnType()
                                            .getReturnType()
                                            .getPrimitiveType()
                                            .name()));
                }
                if (!signature.getVariableDefs().isEmpty()) {
                    task.put("inputs", handleTaskParameters(signature.getVariableDefs()));
                }
            }
        }
        return task;
    }

    private Map<String, Object> buildSaddleBagStructs(
            ConfigEvaluator configEvaluator, List<LHStructDefBuildItem> structDefs)
            throws IntrospectionException {

        Map<String, Object> structs = new LinkedHashMap<>();

        for (LHStructDefBuildItem item : structDefs) {
            ResolvedConfig resolved =
                    resolveConfigExpression(configEvaluator, item.toRecordable().getName());
            Map<String, Object> internalStruct = new LinkedHashMap<>();

            structs.put(resolved.name(), internalStruct);
            internalStruct.put("configName", resolved.configKey());
            internalStruct.put("description", item.toRecordable().getDescription());
            internalStruct.put("properties", buildStruct(item));
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

    private List<Map<String, Object>> buildStruct(LHStructDefBuildItem structDef)
            throws IntrospectionException {

        LHStructDefType structDefType =
                new LHStructDefType(structDef.toRecordable().getBeanClass());
        List<LHStructProperty> properties = structDefType.getStructProperties();

        List<Map<String, Object>> structProperties = new ArrayList<>();

        for (LHStructProperty property : properties) {
            Map<String, Object> props = new LinkedHashMap<>();

            props.put("name", property.getFieldName());
            props.put(
                    "type",
                    property.getPropertyType()
                            .getTypeDefinition()
                            .getPrimitiveType()
                            .name());
            structProperties.add(props);
        }

        return structProperties;
    }

    private List<Map<String, Object>> handleTaskParameters(
            List<LHTaskParameter> lhTaskParameterList) {

        List<Map<String, Object>> parameters = new ArrayList<>();

        for (LHTaskParameter lhTaskParameter : lhTaskParameterList) {
            Map<String, Object> param = new LinkedHashMap<>();

            param.put("name", lhTaskParameter.getVariableName());
            param.put(
                    "type",
                    lhTaskParameter
                            .getVariableDef()
                            .getTypeDef()
                            .getPrimitiveType()
                            .name());

            parameters.add(param);
        }
        return parameters;
    }

    private byte[] serialize(Map<String, Object> data, Format format) {
        try {
            ObjectMapper mapper = format == Format.JSON ? JSON_MAPPER : YAML_MAPPER;
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(new IOException("Failed to serialize saddlebag", e));
        }
    }
}
