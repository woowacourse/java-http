package org.apache.coyote.http11.finder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ResourceFinder {

    public static final String DEFAULT_BODY = "Hello world!";
    private static final String DEFAULT_RESOURCE_PATH = "static";

    private ResourceFinder() {
    }

    public static String find(String resourcePath) {
        if ("/".equals(resourcePath)) {
            return DEFAULT_BODY;
        }

        URL resourceUrl = ResourceFinder.class.getClassLoader().getResource(DEFAULT_RESOURCE_PATH + resourcePath);

        try {
            Path filePath = Path.of(resourceUrl.toURI());

            return new String(Files.readAllBytes(filePath));
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage());
        }
    }
}
