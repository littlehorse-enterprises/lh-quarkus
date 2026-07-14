package io.littlehorse.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the saddle bag manifest generated at build time by the saddle bag extension.
 *
 * <p>The manifest is written to {@code build/saddle-bag/saddle-bag.json} (see the
 * {@code quarkus.littlehorse.saddle.bag.output.*} properties) relative to the project directory,
 * which is the working directory of the integration test run.
 */
public final class SaddleBagManifest {

    public static final Path MANIFEST_FILE = Path.of("build", "saddle-bag", "saddle-bag.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SaddleBagManifest() {}

    public static JsonNode read() {
        try {
            return MAPPER.readTree(Files.readAllBytes(MANIFEST_FILE));
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Unable to read saddle bag manifest: " + MANIFEST_FILE.toAbsolutePath(), e);
        }
    }

    public static JsonNode task(JsonNode manifest, String name) {
        return manifest.path("tasks").path(name);
    }

    public static JsonNode struct(JsonNode manifest, String name) {
        return manifest.path("structs").path(name);
    }

    /**
     * Finds an element inside a manifest array (e.g. {@code configs} or {@code inputs}) whose
     * {@code fieldName} equals {@code value}. Returns a missing node when not found.
     */
    public static JsonNode findByField(JsonNode array, String fieldName, String value) {
        if (array != null && array.isArray()) {
            for (JsonNode element : array) {
                if (value.equals(element.path(fieldName).asText(null))) {
                    return element;
                }
            }
        }
        return MAPPER.missingNode();
    }

    /** Collects every element of a JSON array as plain text values. */
    public static List<String> texts(JsonNode array) {
        List<String> values = new ArrayList<>();
        if (array != null && array.isArray()) {
            array.forEach(element -> values.add(element.asText()));
        }
        return values;
    }
}
