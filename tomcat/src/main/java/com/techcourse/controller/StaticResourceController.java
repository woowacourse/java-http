package com.techcourse.controller;

import com.techcourse.web.StaticResourceHandler;
import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class StaticResourceController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler;

    public StaticResourceController(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.copyFrom(staticResourceHandler.createResponse(request.path()));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        doGet(request, response);
    }
}
