package com.techcourse.controller;

import org.apache.catalina.http.ContentType;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class PageLoadController extends AbstractController {

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return request.getPath().startsWith("/") && ContentType.isValidFileExtension(request.getPath());
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return HttpResponse.createFileOkResponse(request, request.getPath());
    }
}
