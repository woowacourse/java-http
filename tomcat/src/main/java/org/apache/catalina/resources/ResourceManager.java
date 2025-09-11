package org.apache.catalina.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class ResourceManager {

    private static final String STATIC_PATH = "static";

    public byte[] getResponseBody(final String resourcePath) throws IOException {
        final String path = this.resolve(resourcePath);
        if (Objects.equals(path, "/")) {
            return "Hello World!".getBytes();
        }

        final InputStream resourceStream = getResourceAsStream(path);

        if (resourceStream == null) {
            throw new IOException("Resource not found: " + path);
        }

        try (InputStream is = resourceStream) {
            return is.readAllBytes();
        }
    }

    private InputStream getResourceAsStream(final String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(STATIC_PATH + resourcePath);
    }

    protected abstract String resolve(final String resourcePath);
}
