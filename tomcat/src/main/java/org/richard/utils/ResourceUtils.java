package org.richard.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class ResourceUtils {

    public static String createResourceAsString(final String resourcePath) {
        final var resource = findResource(resourcePath);
        final var path = new File(resource.getFile()).toPath();

        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL findResource(final String resourcePath) {
        final var classLoader = ResourceUtils.class.getClassLoader();
        final var resource = classLoader.getResource(resourcePath);

        if (Objects.nonNull(resource)) {
            return resource;
        }

        throw new IllegalArgumentException(String.format("resource does not exists : %s", resourcePath));
    }

    private ResourceUtils() {
    }
}
