package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.StatusCode;

import java.util.Objects;
import org.apache.coyote.http11.util.StaticResourceUtil;

public class StaticResourceHandler {

    public void execute(HttpRequest request, HttpResponse response) {
        setContentType(request, response);
        setBody(request, response);
    }

    private void setContentType(final HttpRequest request, final HttpResponse response) {
        if (request.getPath().endsWith(".html")) {
            response.setContentType("text/html;charset=utf-8");
            return;
        }
        if (request.getPath().endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
            return;
        }
        if (request.getPath().endsWith(".js")) {
            response.setContentType("application/javascript;charset=utf-8");
            return;
        }
        response.setContentType("text/html;charset=utf-8");
    }

    private void setBody(final HttpRequest request, final HttpResponse response) {
        if (Objects.equals(request.getPath(), "/")) {
            response.setStatusCode(StatusCode.OK);
            response.setBodyAndContentLength("Hello world!");
            return;
        }

        String body = StaticResourceUtil.getStaticResource(request.getPath());
        response.setStatusCode(StatusCode.OK);

        if (body == null) {
            response.setStatusCode(StatusCode.NOT_FOUND);
            body = StaticResourceUtil.getStaticResource("/404.html");
        }
        response.setBodyAndContentLength(body);
    }
}
