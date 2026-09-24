package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StatusLine;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addStatusLine(StatusLine.http11(HttpStatus.OK));
        response.forward("/index.html");
    }
}
