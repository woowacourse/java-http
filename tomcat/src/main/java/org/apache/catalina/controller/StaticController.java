package org.apache.catalina.controller;

import org.apache.catalina.http.StatusCode;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.request.HttpRequest;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setBody(request.getPath());
    }
}
