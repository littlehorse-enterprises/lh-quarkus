package io.littlehorse.quarkus.deployment.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.littlehorse.quarkus.config.LHBuildtimeConfig;
import io.littlehorse.quarkus.config.LHBuildtimeConfig.SaddleConfig.BagConfig.Format;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.deployment.item.LHTaskMethodBuildItem;
import io.littlehorse.sdk.common.adapter.LHTypeAdapterRegistry;
import io.littlehorse.sdk.common.proto.ReturnType;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructProperty;
import io.littlehorse.sdk.wfsdk.internal.taskdefutil.LHTaskParameter;
import io.littlehorse.sdk.wfsdk.internal.taskdefutil.LHTaskSignature;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskMethodHandle;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;

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

public class LHSaddleProcessor {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(
            new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

    @BuildStep
    void generateSaddlebag(
            LHBuildtimeConfig config,
            List<LHTaskMethodBuildItem> taskMethods,
            List<LHStructDefBuildItem> structDefs,
            OutputTargetBuildItem outputTarget,
            BuildProducer<GeneratedResourceBuildItem> resources)
            throws IntrospectionException {
        LHBuildtimeConfig.SaddleConfig.BagConfig bag = config.saddle().bag();

        if (!bag.outputEnable()) {
            return;
        }

        String extension = bag.outputFormat().name().toLowerCase();
        String filename = Path.of(bag.outputPath())
                .resolve(bag.outputFilename() + "." + extension)
                .normalize()
                .toString();

        Map<String, Object> saddlebag = buildSaddlebag(taskMethods, structDefs);
        byte[] content = serialize(saddlebag, bag.outputFormat());

        resources.produce(new GeneratedResourceBuildItem(filename, content));

        Path outputFile = outputTarget.getOutputDirectory().resolve(filename).normalize();
        try {
            Files.createDirectories(outputFile.getParent());
            Files.write(outputFile, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate saddlebag file: " + outputFile, e);
        }
    }

    private Map<String, Object> buildSaddlebag(
            List<LHTaskMethodBuildItem> taskMethods, List<LHStructDefBuildItem> structDefs)
            throws IntrospectionException {

        Map<String, Object> tasks = buildSaddleBagTasks(taskMethods);
        Map<String, Object> structs = buildSaddleBagStructs(structDefs);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("tasks", tasks);
        root.put("structs", structs);
        return root;
    }

    private Map<String, Object> buildSaddleBagTasks(List<LHTaskMethodBuildItem> taskMethods) {
        Map<String, Object> tasks = new LinkedHashMap<>();

        for (LHTaskMethodBuildItem item : taskMethods) {
            tasks.put(item.toRecordable().getName(), buildSaddleBagTask(item));
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
                task.put("returnType", resolveTaskReturnType(signature.getReturnType()));
                task.put("javaReturnType", method.getReturnType().getSimpleName());
                task.put("parameters", handleTaskParameters(signature.getVariableDefs()));
            }
        }
        return task;
    }

    private Map<String, Object> buildSaddleBagStructs(List<LHStructDefBuildItem> structDefs)
            throws IntrospectionException {

        Map<String, Object> structs = new LinkedHashMap<>();

        for (LHStructDefBuildItem item : structDefs) {
            Map<String, Object> internalStruct = new LinkedHashMap<>();

            structs.put(item.toRecordable().getName(), internalStruct);
            internalStruct.put("properties", buildStruct(item));
        }
        return structs;
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
            props.put("type", property.getPropertyType().getClassType().getSimpleName());
            structProperties.add(props);
        }

        return structProperties;
    }

    private List<Map<String, Object>> handleTaskParameters(
            List<LHTaskParameter> lhTaskParameterList) {

        List<Map<String, Object>> parameters = new ArrayList<>();

        for (LHTaskParameter lhTaskParameter : lhTaskParameterList) {
            Map<String, Object> param = new LinkedHashMap<>();

            param.put(
                    lhTaskParameter.getVariableName(),
                    lhTaskParameter.getParameterType().getSimpleName());
            parameters.add(param);
        }
        return parameters;
    }

    private static String resolveTaskReturnType(ReturnType returnType) {
        if (!returnType.hasReturnType()) {
            return "VOID";
        }

        TypeDefinition typeDefinition = returnType.getReturnType();
        return switch (typeDefinition.getDefinedTypeCase()) {
            case PRIMITIVE_TYPE -> typeDefinition.getPrimitiveType().name();
            case STRUCT_DEF_ID -> "STRUCT:" + typeDefinition.getStructDefId().getName();
            case INLINE_ARRAY_DEF -> "ARRAY";
            case DEFINEDTYPE_NOT_SET -> "VOID";
        };
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
