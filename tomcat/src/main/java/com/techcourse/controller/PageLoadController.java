package com.techcourse.controller;

import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.StatusLine;

public class PageLoadController extends AbstractController {

    @Override
    public boolean matchesRequest(HttpRequest request) {
        return request.getPath().endsWith(".css") ||
                request.getPath().endsWith(".js") ||
                request.getPath().endsWith(".html");
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.OK),
                request.getContentType(),
                FileReader.loadFileContent(request.getPathWithoutQuery()));
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        throw new IllegalArgumentException("해당 요청을 처리하지 못했습니다.");
    }
}
