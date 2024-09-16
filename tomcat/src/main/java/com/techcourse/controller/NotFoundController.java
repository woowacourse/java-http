package com.techcourse.controller;

import static com.techcourse.controller.RequestPath.NOT_FOUND;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendRedirect(NOT_FOUND.path());
    }
}
