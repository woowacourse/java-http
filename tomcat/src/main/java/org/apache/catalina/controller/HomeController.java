package org.apache.catalina.controller;

import org.apache.catalina.http.StatusCode;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setBody("/index.html");
    }
}
