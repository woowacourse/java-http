package org.apache.coyote.controller;

import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class HomeController extends AbstractController {

    private static final String HOME_MESSAGE = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(HttpStatus.OK);
        response.setContentType(ContentType.TEXT_HTML);
        response.setHeader("Content-Length", HOME_MESSAGE.getBytes().length + " ");
        response.setBody(HOME_MESSAGE);
    }
}
