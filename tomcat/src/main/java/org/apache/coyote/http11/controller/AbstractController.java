package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpMethod method = httpRequest.getMethod();
        switch (method) {
            case GET -> doGet(httpRequest, httpResponse);
            case POST -> doPost(httpRequest, httpResponse);
            default -> httpResponse.setHttpResponse(ResponseEntity.notFound(""));
        }
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) { /* NOOP */ }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) { /* NOOP */ }
}

