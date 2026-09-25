package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class StaticResourceController extends AbstractController {

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css;charset=utf-8",
            "js", "application/javascript;charset=utf-8",
            "svg", "image/svg+xml"
    );

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        InputStream resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + request.getPath());

        if (resourceStream == null) {
            response.notFound();
            return;
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(resourceStream)) {
            byte[] body = inputStream.readAllBytes();
            response.ok(contentType(request.getPath()), body);
        }
    }

    private String contentType(String path) {
        int extensionDelimiter = path.lastIndexOf('.');
        if (extensionDelimiter == -1) {
            return "application/octet-stream";
        }

        String extension = path.substring(extensionDelimiter + 1);
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }
}
