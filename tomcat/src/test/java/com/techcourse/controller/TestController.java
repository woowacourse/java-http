package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;

public class TestController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(new StatusLine(HttpStatus.OK), new ResponseHeader(), "GET body");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(new StatusLine(HttpStatus.OK), new ResponseHeader(), "POST body");
    }
}
