package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        String resource = request.getPath();

        try {
            String responseBody = loadResourceContent(resource);
            String contentType = getContentType(resource);
            buildOkResponse(responseBody, contentType, response);
        } catch (Exception e) {
            buildRedirectResponse("/404.html", response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        throw new RuntimeException();
    }

    private String loadResourceContent(String resource) throws IOException {
        String resourcePath = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(RESOURCE_BASE_PATH + resource))
                .getPath();

        try (FileInputStream file = new FileInputStream(resourcePath)) {
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void buildOkResponse(String responseBody, String contentType, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("200 OK")
                .withResponseBody(responseBody)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("302 Found")
                .addHeader("Location", location);
    }

    private String getContentType(String resource) {
        if (resource.endsWith(".css")) {
            return "text/css";
        } else if (resource.endsWith(".js")) {
            return "application/javascript";
        } else if (resource.endsWith(".ico")) {
            return "image/x-icon";
        } else if (resource.endsWith(".html")) {
            return "text/html";
        } else {
            return "text/plain";
        }
    }
}
