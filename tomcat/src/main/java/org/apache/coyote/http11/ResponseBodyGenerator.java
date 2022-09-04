package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseBodyGenerator {

    private static final String ROOT_PAGE_REQUEST_URI = "/";
    private static final String ROOT_PAGE = "Hello world!";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String LOGIN_HTML_PATH = "/login.html";
    private static final String NOT_FOUND_MESSAGE = "404";

    public String generate(String resource) throws IOException {
        if (resource.equals(ROOT_PAGE_REQUEST_URI)) {
            return ROOT_PAGE;
        }
        return generateResponseBodyByFile(resource);
    }

    private String generateResponseBodyByFile(String resource) throws IOException {
        String resourceName = getResourceName(resource);
        URL url = getClass().getClassLoader()
                .getResource(resourceName);
        if (url == null) {
            return NOT_FOUND_MESSAGE;
        }
        Path path = new File(url.getFile()).toPath();
        return Files.readString(path);
    }

    private String getResourceName(String resource) {
        if (isLoginRequest(resource)) {
            return STATIC_RESOURCE_PATH + LOGIN_HTML_PATH;
        }
        return STATIC_RESOURCE_PATH + resource;
    }

    private static boolean isLoginRequest(String resource) {
        return resource.contains("login");
    }
}
