package org.apache.catalina.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.nio.charset.StandardCharsets;

public class HomeController extends AbstractController {

    private static final byte[] GREETING = "Hello world!".getBytes(StandardCharsets.UTF_8);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setBody(GREETING);
    }
}
