package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResourceHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {

    private final StaticResourceHandler resourceHandler;

    public StaticResourceController(StaticResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourcePath = request.getRequestLine().getPath();
        try {
            resourceHandler.handle(resourcePath, HttpStatus.OK, response);
        } catch (UncheckedServletException exception) {
            resourceHandler.handle("/404.html", HttpStatus.NOT_FOUND, response);
        }
    }
}
