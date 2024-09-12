package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ErrorController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setInternalServerError();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setInternalServerError();
    }
}
