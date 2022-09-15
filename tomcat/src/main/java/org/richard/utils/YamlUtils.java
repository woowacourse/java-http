package org.richard.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class YamlUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static <T> T readPropertyAsObject(final String propertyFileName, Class<T> clazz) {
        final var resource = YamlUtils.class.getClassLoader().getResource(propertyFileName);
        validateExistence(resource, propertyFileName);

        try {
            final var uri = resource.toURI();
            return objectMapper.readValue(new File(uri), clazz);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validateExistence(final URL resource, final String fileName) {
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException(String.format("Requested file does not exist : %s", fileName));
        }
    }

    private YamlUtils() {
    }
}
