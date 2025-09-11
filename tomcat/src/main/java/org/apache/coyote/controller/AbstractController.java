package org.apache.coyote.controller;

import java.util.Objects;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        if (Objects.equals(request.getRequestMethod(), "GET")) {
            doGet(request, response);
            return;
        }

        if (Objects.equals(request.getRequestMethod(), "POST")) {
            doPost(request, response);
            return;
        }

        String body = "404 Not Found";
        response.status(HttpStatus.NOTFOUND);
        response.contentType("text/html; charset=utf-8");
        response.body(body.getBytes());
    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception { /* NOOP */ }

    protected void doGet(Http11Request request, Http11Response response) throws Exception { /* NOOP */ }
}
