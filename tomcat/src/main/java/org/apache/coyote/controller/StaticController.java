package org.apache.coyote.controller;

import org.apache.coyote.http.StatusCode;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setBody(request.getPath());
    }
}
