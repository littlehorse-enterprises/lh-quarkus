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

import io.littlehorse.quarkus.saddle.config.LHSaddleBagBuildtimeConfig.SaddleConfig.BagConfig.OutputConfig.Format;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class LHSaddleBagProcessorTest {

    private final LHSaddleBagProcessor processor = new LHSaddleBagProcessor();

    @Test
    void shouldProduceSameContentAcrossAllFormats() {
        Map<String, Object> saddlebag = sampleSaddlebag();

        Map<String, Object> fromJson = roundTrip(saddlebag, Format.JSON);
        Map<String, Object> fromYaml = roundTrip(saddlebag, Format.YAML);
        Map<String, Object> fromProperties = roundTrip(saddlebag, Format.PROPERTIES);

        assertThat(normalize(fromJson))
                .isEqualTo(normalize(fromYaml))
                .isEqualTo(normalize(fromProperties))
                .isEqualTo(normalize(saddlebag));
    }

    private Map<String, Object> roundTrip(Map<String, Object> saddlebag, Format format) {
        byte[] serialized = processor.serialize(saddlebag, format);
        System.out.println("Serialized " + format + ":\n" + new String(serialized) + "\n");
        return processor.deserialize(serialized, format);
    }

    private Map<String, Object> sampleSaddlebag() {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("name", "example-saddle-bag");
        root.put("title", "Example Saddle Bag");
        root.put("author", "LittleHorse");
        root.put("description", "An example saddle bag");
        root.put("version", "1.2-SNAPSHOT");

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("tags", List.of("test", "example"));
        metadata.put("licence", "MIT");
        metadata.put("documentation-url", "https://example.com/docs");
        root.put("metadata", metadata);

        Map<String, Object> tasks = new LinkedHashMap<>();

        Map<String, Object> addNumbers = new LinkedHashMap<>();
        addNumbers.put("output", Map.of("type", "INT"));
        addNumbers.put(
                "inputs",
                List.of(Map.of("name", "a", "type", "INT"), Map.of("name", "b", "type", "INT")));
        addNumbers.put("config-name", "task.add-numbers.name");
        addNumbers.put("description", "Adds two integers and returns their sum");
        tasks.put("add-numbers", addNumbers);

        Map<String, Object> notification = new LinkedHashMap<>();
        notification.put("output", Map.of("type", "STR"));
        notification.put("config-name", "task.send-notification.name");
        notification.put("description", "Sends a notification");

        List<Map<String, Object>> configs = new ArrayList<>();
        Map<String, Object> urlConfig = new LinkedHashMap<>();
        urlConfig.put("key", "notification.service.url");
        urlConfig.put("description", "Notification service base URL");
        urlConfig.put("sensitive", false);
        configs.add(urlConfig);
        Map<String, Object> apiKeyConfig = new LinkedHashMap<>();
        apiKeyConfig.put("key", "notification.service.api-key");
        apiKeyConfig.put("description", "API key for the notification service");
        apiKeyConfig.put("sensitive", true);
        apiKeyConfig.put("default-value", "5000");
        configs.add(apiKeyConfig);
        notification.put("configs", configs);
        tasks.put("send-notification", notification);

        root.put("tasks", tasks);

        Map<String, Object> structs = new LinkedHashMap<>();
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("config-name", "struct.order.name");
        order.put("description", "Represents a customer order");
        order.put(
                "properties",
                List.of(
                        Map.of("name", "price", "type", "DOUBLE"),
                        Map.of("name", "quantity", "type", "INT")));
        structs.put("order", order);
        root.put("structs", structs);

        return root;
    }

    /**
     * Normalizes a deserialized structure so the contents can be compared across formats. The
     * properties format is untyped (booleans and numbers become strings) and represents lists as
     * index-keyed maps, so scalars are coerced to strings and numeric-keyed maps are turned into
     * lists.
     */
    private Object normalize(Object value) {
        if (value instanceof Map<?, ?> map) {
            if (!map.isEmpty() && map.keySet().stream().allMatch(this::isInteger)) {
                List<Object> list = new ArrayList<>();
                Map<Integer, Object> byIndex = new TreeMap<>();
                map.forEach((key, element) ->
                        byIndex.put(Integer.parseInt(String.valueOf(key)), element));
                byIndex.values().forEach(element -> list.add(normalize(element)));
                return list;
            }
            Map<String, Object> normalized = new LinkedHashMap<>();
            map.forEach((key, element) -> normalized.put(String.valueOf(key), normalize(element)));
            return normalized;
        }
        if (value instanceof List<?> list) {
            List<Object> normalized = new ArrayList<>();
            list.forEach(element -> normalized.add(normalize(element)));
            return normalized;
        }
        return String.valueOf(value);
    }

    private boolean isInteger(Object key) {
        if (!(key instanceof String string)) {
            return false;
        }
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
