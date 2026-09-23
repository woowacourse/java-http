package com.techcourse.controller;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private final StaticResourceResolver staticResourceResolver = new StaticResourceResolver();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourcePath = request.getResourcePath();

        staticResourceResolver.read(resourcePath)
                .ifPresentOrElse(
                        body -> response.send(HttpStatus.OK, resourcePath, body),
                        () -> sendErrorPage(response, HttpStatus.NOT_FOUND)
                );
    }

    private void sendErrorPage(HttpResponse response, HttpStatus status) {
        String errorPage = status.getErrorPage();

        staticResourceResolver.read(errorPage)
                .ifPresentOrElse(
                        body -> response.send(status, errorPage, body),
                        () -> response.send(status, errorPage, new byte[0])
                );
    }
}
