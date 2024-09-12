package com.techcourse.controller;

import org.apache.coyote.ForwardResult;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    public ForwardResult execute(HttpRequest request, HttpResponse response) {
        return ForwardResult.ofRedirect("404.html");
    }
}
