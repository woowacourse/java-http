package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException, URISyntaxException {
        return serve(request.getRequestTarget());
    }

    public HttpResponse serve(String path) throws IOException, URISyntaxException {
        URL resource = ClassLoader.getSystemResource("static" + path);
        if (resource == null) {
            return HttpResponse.create("404 Not Found", "text/html",
                    read(ClassLoader.getSystemResource("static/404.html")));
        }
        return HttpResponse.create("200 OK", contentType(path), read(resource));
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    private String read(URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()));
    }
}
