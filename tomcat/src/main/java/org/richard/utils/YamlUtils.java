package org.richard.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class YamlUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static <T> T readPropertyAsObject(final String propertyFileName, Class<T> clazz) {
        final var resource = YamlUtils.class.getClassLoader().getResource(propertyFileName);

        try {
            final var uri = resource.toURI();
            return objectMapper.readValue(new File(uri), clazz);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private YamlUtils() {
    }
}
