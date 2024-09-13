package org.apache.coyote.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ResourceFinder {

    private static final String RESOURCE_START_PATH = "static";

    private ResourceFinder() {
    }

    public static String findBy(String resourceLocation) {
        URL resourceUrl = ResourceFinder.class.getClassLoader()
                .getResource(RESOURCE_START_PATH + resourceLocation);
        validateResourceUrl(resourceUrl);

        try {
            Path filePath = Path.of(resourceUrl.toURI());
            return Files.readString(filePath);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException("URI 형식이 잘못되었습니다.");
        }
    }

    private static void validateResourceUrl(URL resourceUrl) {
        if (resourceUrl == null) {
            throw new IllegalArgumentException("존재하지 않는 URL 입니다.");
        }
    }
}
