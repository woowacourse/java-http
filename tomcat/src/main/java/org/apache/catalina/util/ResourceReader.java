package org.apache.catalina.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class ResourceReader {

    private static final String RESOURCE_PATH = "static";
    private static final String QUERY_PARAM_DELIMITER = "\\?";

    public static void serveResource(String resourceUri, HttpResponse response) throws IOException {
        String path = resourceUri.split(QUERY_PARAM_DELIMITER)[0];
        Path resourcePath = getResourcePath(path);
        String body = Files.readString(resourcePath);
        String contentType = Files.probeContentType(resourcePath);

        response.setBody(body, contentType);
        response.setStatus(HttpStatus.OK);
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
