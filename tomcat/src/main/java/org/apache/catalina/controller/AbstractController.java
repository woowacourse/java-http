package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final RequestLine requestLine = request.requestLine();

        if (requestLine.isGetMethod()) {
            doGet(request, response);
            return;
        }

        if (requestLine.isPostMethod()) {
            doPost(request, response);
            return;
        }

        response.sendForwardResponse(HttpStatusCode.NOT_FOUND, "/404.html");
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        response.sendForwardResponse(HttpStatusCode.NOT_FOUND, "/404.html");
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.sendForwardResponse(HttpStatusCode.NOT_FOUND, "/404.html");
    }
}
