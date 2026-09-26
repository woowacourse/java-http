package com.techcourse.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // HTTP method에 따라 doGet 또는 doPost로 분기한다.
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> methodNotAllowed(response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected String readResource(String path) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new FileNotFoundException(path);
        }
        return Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    protected String allowedMethods() {
        return "GET, POST";
    }

    private void methodNotAllowed(HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.addHeader("Allow", allowedMethods());
        response.setContentType(ContentType.TEXT);
        response.setBody(HttpStatus.METHOD_NOT_ALLOWED.getMessage());
    }

}
