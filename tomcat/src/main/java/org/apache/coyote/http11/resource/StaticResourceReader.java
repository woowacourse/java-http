package org.apache.coyote.http11.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class StaticResourceReader {

    public Optional<String> read(String resourcePath) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return Optional.empty();
            }
            return Optional.of(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
