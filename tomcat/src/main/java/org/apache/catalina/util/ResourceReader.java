package org.apache.catalina.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceReader {

    private static final String RESOURCE_PATH = "static";
    private static final String QUERY_PARAM_DELIMITER = "\\?";
    private static final int PATH_INDEX = 0;

    public static ResourceFile readResource(String resourceUri) throws IOException {
        String path = resourceUri.split(QUERY_PARAM_DELIMITER)[PATH_INDEX];
        Path resourcePath = getResourcePath(path);
        String body = Files.readString(resourcePath);
        String contentType = Files.probeContentType(resourcePath);

        return new ResourceFile(contentType, body);
    }

    private static Path getResourcePath(String path) {
        ClassLoader classLoader = ResourceReader.class.getClassLoader();

        URL resourceUrl = classLoader.getResource(RESOURCE_PATH + path);
        if (resourceUrl == null) {
            resourceUrl = classLoader.getResource(RESOURCE_PATH + "/404.html");
        }

        return Path.of(resourceUrl.getPath());
    }
}
