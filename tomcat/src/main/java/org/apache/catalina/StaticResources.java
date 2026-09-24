package org.apache.catalina;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class StaticResources {
    private static final String STATIC_RESOURCE_PREFIX = "static";

    private StaticResources() {}

    public static Optional<Path> find(String path) throws URISyntaxException {
        URL resourceUrl = StaticResources.class.getClassLoader().getResource(STATIC_RESOURCE_PREFIX + path);
        if (resourceUrl == null) {
            return Optional.empty();
        }
        return Optional.of(Path.of(resourceUrl.toURI()))
                .filter(Files::isRegularFile);
    }
}
