package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {

    private static final String STATIC_PATH = "static";

    private static final String SLASH = "/";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";

    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String JS_CONTENT_TYPE = "text/javascript";
    private static final String HTML_CONTENT_TYPE = "text/html";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        render(request.getPath(), response);
    }

    public void render(String path, HttpResponse response) {
        try {
            URL resource = getClass().getClassLoader().getResource(findPath(path));
            if (resource == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                return;
            }

            Path filePath = Paths.get(resource.toURI());
            response.setHeader("Content-Type", findContentType(filePath) + ";charset=utf-8");
            response.setBody(findResponseBody(filePath));
        } catch (IOException | URISyntaxException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String findPath(String path) {
        if (!path.isBlank()) {
            if (!path.equals(SLASH) && !path.contains(EXTENSION_DELIMITER)) {
                path += HTML_EXTENSION;
            }
        }

        return STATIC_PATH + path;
    }

    private String findContentType(Path filePath) {
        if (filePath.toString().endsWith(CSS_EXTENSION)) {
            return CSS_CONTENT_TYPE;
        } else if (filePath.toString().endsWith(JS_EXTENSION)) {
            return JS_CONTENT_TYPE;
        }
        return HTML_CONTENT_TYPE;
    }

    private String findResponseBody(Path filePath) throws IOException {
        if (Files.isDirectory(filePath)) {
            return "Hello world!";
        }

        return Files.readString(filePath);
    }
}
