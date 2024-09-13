package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.constants.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceReader;

public class NotFoundPageController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        responseNotFound(response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        responseNotFound(response);
    }

    private static void responseNotFound(final HttpResponse response) throws IOException {
        String body = StaticResourceReader.read("/404.html");
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        response.setBody(body);
    }
}
