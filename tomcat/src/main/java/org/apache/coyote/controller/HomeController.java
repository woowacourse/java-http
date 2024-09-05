package org.apache.coyote.controller;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class HomeController extends AbstractController {


    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = "Hello World!";
        response.setStatusLine(HttpStatus.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8 ");
        response.setHeader("Content-Length", body.getBytes().length + " ");
        response.setBody("Hello World!");
    }
}
