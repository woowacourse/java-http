package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundController extends AbstractController {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.redirectTo(NOT_FOUND_PAGE);
    }
}
