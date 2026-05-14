package io.littlehorse.quarkus.deployment.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.littlehorse.quarkus.config.LHBuildtimeConfig;
import io.littlehorse.quarkus.config.LHBuildtimeConfig.SaddleConfig.BagConfig.Format;
import io.littlehorse.quarkus.deployment.item.LHStructDefBuildItem;
import io.littlehorse.quarkus.deployment.item.LHTaskMethodBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            BuildProducer<GeneratedResourceBuildItem> resources) {
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
            List<LHTaskMethodBuildItem> taskMethods, List<LHStructDefBuildItem> structDefs) {
        Map<String, Object> tasks = new LinkedHashMap<>();
        for (LHTaskMethodBuildItem item : taskMethods) {
            tasks.put(item.toRecordable().getName(), null);
        }
        Map<String, Object> structs = new LinkedHashMap<>();
        for (LHStructDefBuildItem item : structDefs) {
            structs.put(item.toRecordable().getName(), null);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("tasks", tasks);
        root.put("structs", structs);
        return root;
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
