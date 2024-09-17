package org.apache.catalina.controller;

import org.apache.catalina.http.HeaderName;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class TestController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        response.addHeader(HeaderName.CONTENT_LENGTH, "0");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.addHeader(HeaderName.CONTENT_LENGTH, "1");
    }
}
