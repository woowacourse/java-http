package com.techcourse.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendStaticHtml(request.getTarget());
    }
}
