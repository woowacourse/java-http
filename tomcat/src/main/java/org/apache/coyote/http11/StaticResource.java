package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public final class StaticResource {

    private static final String RESOURCE_FILE_PREFIX = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";

    private StaticResource() {
    }

    public static void serve(HttpResponse response, String resourcePath) throws IOException {
        serve(response, resourcePath, HttpStatus.OK);
    }

    public static void serve(HttpResponse response, String resourcePath, HttpStatus status)
        throws IOException {
        URL resource = StaticResource.class.getClassLoader()
            .getResource(RESOURCE_FILE_PREFIX + resourcePath);
        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            return;
        }

        response.setStatus(status);
        response.setBody(readAllBytes(resource), resolveContentType(resourcePath));
    }

    private static byte[] readAllBytes(URL resource) throws IOException {
        try {
            return Files.readAllBytes(Path.of(resource.toURI()));
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    private static String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(CSS_EXTENSION)) {
            return "text/css";
        }
        if (resourcePath.endsWith(JS_EXTENSION)) {
            return "text/javascript";
        }
        if (resourcePath.endsWith(HTML_EXTENSION)) {
            return "text/html";
        }

        return null;
    }
}
