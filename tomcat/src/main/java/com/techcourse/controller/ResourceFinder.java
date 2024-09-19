package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceFinder {

    public static final String FILE_DELIMITER = ".";
    public static final String STATIC_RESOURCE_PREFIX = "static";
    public static final String NOT_FOUND_LOCATION = "/404.html";
    public static final String HOME_LOCATION = "/index";
    public static final String FORMATTED_JAVASCRIPT_EXTENSION = "javascript";

    public static void setStaticResponse(HttpRequest request, HttpResponse response) {
        String fileName = getLocation(request);
        String extension = request.getExtension();
        String fullName = fileName + FILE_DELIMITER + extension;

        URL url = getStaticUrl(fullName);
        response.setContentType(String.format("text/%s;charset=utf-8", getFormattedExtension(extension)));

        String content = getContent(url);
        response.setBody(content);
    }

    private static String getContent(URL url) {
        try {
            Path filePath = Path.of(url.toURI());
            return Files.readString(filePath);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getStaticUrl(String fullName) {
        ClassLoader classLoader = ResourceFinder.class.getClassLoader();
        URL resource = classLoader.getResource(STATIC_RESOURCE_PREFIX + fullName);
        if (Objects.isNull(resource)) {
            resource = classLoader.getResource(STATIC_RESOURCE_PREFIX + NOT_FOUND_LOCATION);
        }
        return resource;
    }

    private static String getLocation(HttpRequest request) {
        return request.getLocation().equals("/") ? HOME_LOCATION : request.getLocation();
    }

    private static String getFormattedExtension(String extension) {
        return extension.equals("js") ? FORMATTED_JAVASCRIPT_EXTENSION : extension;
    }
}
