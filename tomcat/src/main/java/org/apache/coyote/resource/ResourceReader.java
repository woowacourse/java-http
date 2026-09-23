package org.apache.coyote.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ResourceReader {

    private final ClassLoader classLoader;

    public ResourceReader() {
        this(ResourceReader.class.getClassLoader()
        );
    }

    ResourceReader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Optional<byte[]> read(final String resourcePath) throws IOException {

        try (final InputStream inputStream =
                     classLoader.getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(
                    inputStream.readAllBytes()
            );
        }
    }
}