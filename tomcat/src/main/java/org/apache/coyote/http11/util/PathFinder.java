package org.apache.coyote.http11.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathFinder {

    private static final String RESOURCE_ROOT_DIRECTORY_PATH = "static";

    public static Path findPath(String resourceName) throws URISyntaxException {
        final URL resource =
                PathFinder.class.getClassLoader().getResource(RESOURCE_ROOT_DIRECTORY_PATH + resourceName);
        return Paths.get(resource.toURI());
    }
}
