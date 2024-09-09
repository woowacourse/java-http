package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class BasicController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return HttpResponse.basicResponse();
    }
}
