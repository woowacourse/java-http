package com.techcourse.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusCode;

public class StaticController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = resourceReader.read(request.getRequestTarget());
        if (body == null) {
            response.setStatus(StatusCode.NOT_FOUND);
            return;
        }
        response.setBody(body);
    }

}
