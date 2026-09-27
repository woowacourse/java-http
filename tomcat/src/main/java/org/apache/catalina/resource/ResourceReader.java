package org.apache.catalina.resource;

import org.apache.coyote.http11.response.ContentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ResourceReader {

    private ResourceReader() {
    }

    public static Optional<Resource> read(final String path) throws IOException {
        try (final var inputStream =
                     ResourceReader.class.getClassLoader().getResourceAsStream("static" + path)) {

            if (inputStream == null) {
                return Optional.empty();
            }
            final var content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Optional.of(new Resource(content, ContentType.from(path)));
        }
    }
}
