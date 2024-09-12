package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StatusCode;

public class StaticResourceController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        String resource = request.getPath();

        try {
            String responseBody = loadResourceContent(resource);
            String contentType = response.getContentType(resource);
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
        response.withStatusCode(StatusCode.OK)
                .withResponseBody(responseBody)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode(StatusCode.FOUND)
                .addHeader("Location", location);
    }
}
