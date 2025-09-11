package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.handler.statics.util.StaticResourceUtils;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.method().toUpperCase()) {
            case "GET"  -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default     -> StaticResourceUtils.serve(response, "404.html", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
