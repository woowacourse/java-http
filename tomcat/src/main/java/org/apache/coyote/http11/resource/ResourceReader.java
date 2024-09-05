package org.apache.coyote.http11.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceReader {

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

    private static final String ROOT_PATH = "/";

    private static final String DEFAULT_PATH = "static";

    private static final String DEFAULT_RESOURCE = "Hello world!";

    public static String readString(final String resourcePath) throws IOException {
        if (resourcePath.equals(ROOT_PATH)) {
            return DEFAULT_RESOURCE;
        }
        final var url = CLASS_LOADER.getResource(DEFAULT_PATH + resourcePath);
        final var path = Path.of(url.getPath());
        return Files.readString(path);
    }
}
