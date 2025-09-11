package org.apache.coyote.http11.util;

import ch.qos.logback.core.util.FileUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.exception.BadRequestException;

public class StaticResourceUtil {

    private static final String STATIC_PREFIX = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String EXTENSION_DELIMITER = ".";

    public static String getStaticResource(final String resourcePath) {
        final var wholeResourcePath = getWholeResourcePath(resourcePath);

        try (final var inputStream = FileUtil.class.getClassLoader().getResourceAsStream(wholeResourcePath)) {
            if (inputStream == null) {
                throw new BadRequestException("Resource not found: " + wholeResourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getWholeResourcePath(final String resourcePathPart) {
        if (resourcePathPart.contains(EXTENSION_DELIMITER)) {
            return STATIC_PREFIX + resourcePathPart;
        }
        return STATIC_PREFIX + resourcePathPart + DEFAULT_EXTENSION;
    }
}
