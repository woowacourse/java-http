package org.apache.catalina;

import com.techcourse.exception.UncheckedServletException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceHandler {

    private static final String STATIC_RESOURCE_PATH = "static";

    public void handle(String resourcePath, HttpStatus status, HttpResponse response) {
        byte[] body = readResourceAsBytes(resourcePath);
        response.setStatus(status);
        response.setHeader("Content-Type", resolveContentType(resourcePath));
        response.setBody(body);
    }

    private byte[] readResourceAsBytes(String resourcePath) {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + resourcePath);
        if (resource == null) {
            throw new UncheckedServletException(new FileNotFoundException(resourcePath));
        }

        try {
            return Files.readAllBytes(Path.of(resource.toURI()));
        } catch (IOException | URISyntaxException exception) {
            throw new UncheckedServletException(exception);
        }
    }

    private String resolveContentType(String resourcePath) {
        int index = resourcePath.lastIndexOf('.');
        if (index < 0) {
            return "text/html;charset=utf-8";
        }

        String extension = resourcePath.substring(index + 1);
        if (extension.equals("svg")) {
            return "image/svg+xml";
        }
        return "text/" + extension + ";charset=utf-8";
    }
}
