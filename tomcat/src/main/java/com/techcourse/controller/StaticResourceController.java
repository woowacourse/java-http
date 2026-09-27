package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        StaticResource.serve(response, request.getPath());
    }
}
