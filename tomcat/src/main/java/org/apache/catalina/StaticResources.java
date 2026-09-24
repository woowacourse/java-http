package org.apache.catalina;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class StaticResources {
    private static final String STATIC_RESOURCE_PREFIX = "static";

    private StaticResources() {}

    public static Optional<StaticResource> find(String path) throws URISyntaxException, IOException {
        URL resourceUrl = StaticResources.class.getClassLoader().getResource(STATIC_RESOURCE_PREFIX + path);
        if (resourceUrl == null) {
            return Optional.empty();
        }

        Path resourcePath = Path.of(resourceUrl.toURI());
        if (!Files.isRegularFile(resourcePath)) {
            return Optional.empty();
        }
        return Optional.of(StaticResource.from(resourcePath));
    }
}
