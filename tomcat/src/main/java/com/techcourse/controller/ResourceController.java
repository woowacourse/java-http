package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;
import org.apache.coyote.http11.controller.AbstractController;

public class ResourceController extends AbstractController {

    public static final String STATIC = "static";

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        response.generateResponse(STATIC + path, Status.OK);
    }
}
