package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestMethod;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasMethod(HttpRequestMethod.GET)) {
            doGet(request, response);
        }

        if (request.hasMethod(HttpRequestMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void serveStaticFile(String path, HttpResponse response, String contentType) throws IOException, URISyntaxException {
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
