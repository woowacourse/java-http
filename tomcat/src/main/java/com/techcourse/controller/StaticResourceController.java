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

public class StaticResourceController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource("static" + request.getUri().getPath());
        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(ContentType.TEXT);
            response.setBody("요청한 파일을 찾을 수 없습니다.");
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.from(request.getUri().getPath()));
        response.setBody(Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8));
    }
}
