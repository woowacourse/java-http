package com.techcourse.controller;

import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class RootController extends AbstractController {

    public static final String ROOT_PATH = "/";
    private static final String DEFAULT_PAGE_CONTENT = "Hello world!";

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return ROOT_PATH.equals(request.getPath());
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setBody(DEFAULT_PAGE_CONTENT);
    }
}
