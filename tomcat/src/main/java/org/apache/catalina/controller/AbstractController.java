package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .addHeader("ALLOW", "GET, POST")
                    .body("허용되지 않는 메서드입니다.");
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
