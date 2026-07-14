package io.littlehorse.quarkus.saddle.deployment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Immutable model of a generated saddle bag manifest. All optional fields are omitted from the
 * serialized output when {@code null} (see {@link JsonInclude.Include#NON_NULL}).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SaddleBag(
        String name,
        String title,
        String author,
        String description,
        String version,
        Metadata metadata,
        Map<String, Task> tasks,
        Map<String, Struct> structs,
        List<Config> configs) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Metadata(
            List<String> tags,
            String licence,
            @JsonProperty("documentation-url") String documentationUrl,
            @JsonProperty("icon-url") String iconUrl,
            @JsonProperty("support-email") String supportEmail,
            @JsonProperty("docker-image") String dockerImage) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Task(
            Output output,
            List<Input> inputs,
            List<TaskException> exceptions,
            @JsonProperty("config-name") String configName,
            String description,
            List<Config> configs) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Output(Type type) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Input(String name, Type type) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TaskException(String name, String description) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Config(
            String key,
            String description,
            boolean sensitive,
            Type type,
            @JsonProperty("default-value") String defaultValue) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Struct(
            @JsonProperty("config-name") String configName,
            String description,
            List<Property> properties) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Property(String name, Type type) {}

    /**
     * Standardized type descriptor shared by task inputs, task outputs, struct properties, and task
     * configs. Exactly one field is non-null, mirroring the LittleHorse {@code TypeDefinition}
     * {@code oneof}.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Type(String primitive, String struct, ArrayType array, MapType map) {

        public static Type primitive(String primitive) {
            return new Type(primitive, null, null, null);
        }

        public static Type struct(String struct) {
            return new Type(null, struct, null, null);
        }

        public static Type array(Type elements) {
            return new Type(null, null, new ArrayType(new Elements(elements)), null);
        }

        public static Type map(Type key, Type value) {
            return new Type(null, null, null, new MapType(new Key(key), new Value(value)));
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ArrayType(Elements elements) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record MapType(Key key, Value value) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Elements(Type type) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Key(Type type) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Value(Type type) {}
}
