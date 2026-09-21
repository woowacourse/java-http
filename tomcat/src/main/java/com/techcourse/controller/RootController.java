package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setContentType(ContentType.TEXT);
        response.setBody("Hello world!");
    }
}
