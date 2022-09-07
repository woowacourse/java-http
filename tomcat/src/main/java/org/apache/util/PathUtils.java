package org.apache.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static final String RESOURCE_ROOT_DIRECTORY_PATH = "static";

    private PathUtils() {
    }

    public static Path load(String path) throws URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(RESOURCE_ROOT_DIRECTORY_PATH + path);
        if (resource == null) {
            throw new IllegalArgumentException(path + "가 존재하지 않습니다.");
        }
        return Paths.get(resource.toURI());
    }
}
