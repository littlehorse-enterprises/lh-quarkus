package io.littlehorse.quarkus.saddle.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;

@ConfigMapping(prefix = "quarkus.littlehorse")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface LHSaddleBagBuildtimeConfig {

    SaddleConfig saddle();

    interface SaddleConfig {
        BagConfig bag();

        interface BagConfig {
            String name();

            String title();

            String author();

            String description();

            MetadataConfig metadata();

            OutputConfig output();

            interface OutputConfig {
                @WithDefault("true")
                boolean enable();

                @WithDefault("saddle-bag/")
                String path();

                @WithDefault("saddle-bag")
                String filename();

                @WithDefault("yaml")
                Format format();

                enum Format {
                    JSON,
                    YAML
                }
            }

            interface MetadataConfig {
                List<String> tags();

                String licence();

                @WithName("documentation-url")
                String documentationUrl();

                @WithName("icon-url")
                String iconUrl();

                @WithName("support-email")
                String supportEmail();
            }
        }
    }
}
