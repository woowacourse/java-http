package org.apache.util;

import static org.reflections.Reflections.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceUtil {

    private static final String PREFIX = "static";

    public static String getResource(final String requestUri) {
        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(getPath(requestUri)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return responseBody;
    }

    public static Path getPath(final String requestUri) {
        String filePath = ClassLoader.getSystemResource(PREFIX + requestUri)
                .getPath();
        return new File(filePath).toPath();
    }
}
