package org.apache.coyote.http11.controller.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StaticResourceReader {

    private static final String DEFAULT_MIMETYPE = "application/octet-stream";

    private StaticResourceReader() {
    }

    public static byte[] readResource(String fullPath) throws IOException {
        try (InputStream resourceStream = StaticResourceReader.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (resourceStream == null) {
                throw new FileNotFoundException(fullPath);
            }
            return resourceStream.readAllBytes();
        }
    }

    public static String resolveContentType(String resourcePath) {
        try {
            Path path = Path.of(resourcePath);
            String mimeType = Files.probeContentType(path);
            return (mimeType != null ? mimeType : DEFAULT_MIMETYPE);
        } catch (IOException e) {
            return DEFAULT_MIMETYPE;
        }
    }
}
