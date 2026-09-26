package com.techcourse.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String NOT_FOUND_MESSAGE = "요청한 파일을 찾을 수 없습니다.";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        String body;
        try {
            body = readResource(STATIC_RESOURCE_DIRECTORY + request.getUri().getPath());
        } catch (FileNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(ContentType.TEXT);
            response.setBody(NOT_FOUND_MESSAGE);
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.from(request.getUri().getPath()));
        response.setBody(body);
    }

    @Override
    protected String allowedMethods() {
        return "GET";
    }

}
