package com.techcourse.controller;

import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.StatusLine;

public class RootController extends AbstractController {

    public static final String ROOT_PATH = "/";
    private static final String DEFAULT_PAGE_CONTENT = "Hello world!";

    @Override
    public boolean matchesRequest(HttpRequest request) {
        return ROOT_PATH.equals(request.getPath());
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.OK),
                request.getContentType(),
                DEFAULT_PAGE_CONTENT
        );
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        throw new IllegalArgumentException("해당 요청을 처리하지 못했습니다.");
    }
}
