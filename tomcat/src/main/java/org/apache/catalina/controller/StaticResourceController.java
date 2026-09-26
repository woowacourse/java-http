package org.apache.catalina.controller;

import org.apache.catalina.util.StaticResources;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Optional;

public class StaticResourceController implements Controller {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final String path = request.getPath();
        final Optional<String> body = StaticResources.read(path);

        if (body.isEmpty()) {
            response.sendError(HttpStatus.NOT_FOUND, StaticResources.read(NOT_FOUND_PAGE).orElse(""));
            return;
        }
        response.ok(ContentType.from(path), body.get());
    }
}
