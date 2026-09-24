package com.techcourse.api.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public final class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.HTML);
        response.setBody(readResource("/index.html"));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

}
