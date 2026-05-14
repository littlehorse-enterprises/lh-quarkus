package io.littlehorse.quarkus.deployment.processor;

import io.littlehorse.quarkus.config.LHBuildtimeConfig;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LHSaddleProcessor {

    @BuildStep
    void generateSaddlebag(
            LHBuildtimeConfig config,
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
        byte[] content = "hello: world".getBytes();

        resources.produce(new GeneratedResourceBuildItem(filename, content));

        Path outputFile = outputTarget.getOutputDirectory().resolve(filename).normalize();
        try {
            Files.createDirectories(outputFile.getParent());
            Files.write(outputFile, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate saddlebag file: " + outputFile, e);
        }
    }
}
