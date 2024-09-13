package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class NotFoundHandler extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        responseTo404Page(response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        responseTo404Page(response);
    }

    private void responseTo404Page(HttpResponse response) throws IOException {
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setStaticResourceResponse("/404.html");
        response.write();
    }
}
