package io.littlehorse.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.littlehorse")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface LHBuildtimeConfig {
    @WithName("health.enabled")
    @WithDefault("true")
    boolean healthEnabled();

    SaddleConfig saddle();

    interface SaddleConfig {
        BagConfig bag();

        interface BagConfig {
            @WithName("output.enable")
            @WithDefault("false")
            boolean outputEnable();

            @WithName("output.path")
            @WithDefault("META-INF/saddlebag/")
            String outputPath();

            @WithName("output.filename")
            @WithDefault("saddlebag")
            String outputFilename();

            @WithName("output.format")
            @WithDefault("yaml")
            Format outputFormat();

            enum Format {
                JSON,
                YAML
            }
        }
    }
}
