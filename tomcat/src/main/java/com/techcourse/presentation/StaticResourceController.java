package com.techcourse.presentation;

import org.apache.coyote.http11.RequestLine;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        final RequestLine requestLine = request.requestLine();
        return renderStaticPage(requestLine.getUri(), requestLine.getProtocol());
    }

    @Override
    protected String getBasePath() {
        return null;
    }
}
