package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StaticResourceController extends AbstractController {

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript"
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
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            response.ok(contentType(request.getPath()), body);
        }
    }

    private String contentType(String path) {
        int extensionDelimiter = path.lastIndexOf('.');
        if (extensionDelimiter == -1) {
            return "text/plain;charset=utf-8";
        }

        String extension = path.substring(extensionDelimiter + 1);
        String mimeType = MIME_TYPES.getOrDefault(extension, "text/plain");
        return mimeType + ";charset=utf-8";
    }
}
