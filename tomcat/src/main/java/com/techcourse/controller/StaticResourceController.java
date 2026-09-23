package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String NOT_FOUND_MESSAGE = "요청한 파일을 찾을 수 없습니다.";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader()
                .getResource(STATIC_RESOURCE_DIRECTORY + request.getUri().getPath());
        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(ContentType.TEXT);
            response.setBody(NOT_FOUND_MESSAGE);
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.from(request.getUri().getPath()));
        response.setBody(Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8));
    }
}
