package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.endsWith(".css")) {
            serveStaticFile(request.getPath(), response, "text/css;charset=utf-8");
            return;
        }

        if (request.endsWith(".html")) {
            serveStaticFile(request.getPath(), response, "text/html;charset=utf-8");
            return;
        }

        if (request.endsWith(".js")) {
            serveStaticFile(request.getPath(), response, "text/javascript;charset=utf-8");
        }

        final byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        response.setBody(body);
        response.send();
    }

    private void serveStaticFile(String path, HttpResponse response, String contentType) throws IOException, URISyntaxException {
        final var resource = getClass().getClassLoader().getResource("static" + path);
        if (resource != null) {
            final Path resourcePath = Paths.get(resource.toURI());
            byte[] body = Files.readAllBytes(resourcePath);
            response.addHeader("Content-Type", contentType);
            response.setBody(body);
        }
        response.send();
    }
}
