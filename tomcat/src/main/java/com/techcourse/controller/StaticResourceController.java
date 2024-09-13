package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.util.ContentTypeResolver;
import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.constants.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceReader;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        responseStaticResource(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        responseStaticResource(request, response);
    }

    private void responseStaticResource(final HttpRequest request, final HttpResponse response) throws IOException {
        final ContentType contentType = ContentTypeResolver.resolve(request.getUri(), request.getAcceptLine());
        final String body = StaticResourceReader.read(request.getUri());
        response.setHttpStatus(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, contentType.getContentTypeUtf8());
        response.setBody(body);
    }
}
