package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeader;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class IndexController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(StatusCode.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue());
        response.setBody("Hello world!");
    }
}
