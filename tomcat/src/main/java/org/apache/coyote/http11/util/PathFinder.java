package org.apache.coyote.http11.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathFinder {

    private static final String RESOURCE_ROOT_DIRECTORY_PATH = "static";

    public static Path findPath(String resourceName) {
        try {
            URL resource =
                    PathFinder.class.getClassLoader().getResource(RESOURCE_ROOT_DIRECTORY_PATH + resourceName);

            if (resource == null) {
                throw new IllegalArgumentException("Resource Not Found: " + resourceName);
            }
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI Syntax Exception : " + resourceName);
        }
    }
}
