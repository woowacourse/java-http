package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {

    private final String resourceRoot;

    public StaticResourceController(String resourceRoot) {
        this.resourceRoot = resourceRoot;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        serve(request.getPath(), response);
    }

    public void serve(String path, HttpResponse response) throws IOException {
        if (!path.startsWith("/") || path.contains("..")) {
            response.sendError(HttpStatus.NOT_FOUND);
            return;
        }

        String resourceName = resourceRoot + path;

        try (InputStream resource = getClass()
                .getClassLoader()
                .getResourceAsStream(resourceName)) {
            if (resource == null) {
                response.sendError(HttpStatus.NOT_FOUND);
                return;
            }

            response.setBody(resource.readAllBytes(), contentType(path));
        }
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }
}
