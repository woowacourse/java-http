package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.http.StatusCode.OK;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.send(request.getResource(), OK);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return false;
    }
}
