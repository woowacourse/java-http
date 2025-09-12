package org.apache.catalina.controller;

import org.apache.catalina.controller.util.StaticResourceReader;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.request.dto.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private final String base;
    private final String defaultDocument;

    public StaticResourceController(String base, String defaultDocument) {
        this.base = base;
        this.defaultDocument = defaultDocument;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resourcePath = request.path().replaceFirst("^/static/?", "");
        String normalizedResourcePath = normalizePath(resourcePath);
        String fullPath = base + "/" + normalizedResourcePath;
        byte[] bytes = StaticResourceReader.readResource(fullPath);
        String contentType = StaticResourceReader.resolveContentType(normalizedResourcePath);

        response.status(HttpStatus.OK)
                .contentType(contentType)
                .write(bytes);
    }

    private String normalizePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.isEmpty()) {
            path = defaultDocument;
        }
        return path;
    }
}
