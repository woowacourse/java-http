package com.techcourse.servlet;

import static org.apache.coyote.http11.HttpStatus.METHOD_NOT_SUPPORTED;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Servlet;

public abstract class HttpServlet implements Servlet {

    protected abstract void doGet(HttpRequest req, HttpResponse resp);
    protected abstract void doPost(HttpRequest req, HttpResponse resp);
    protected abstract void doPut(HttpRequest req, HttpResponse resp);
    protected abstract void doDelete(HttpRequest req, HttpResponse resp);
    protected abstract void doPatch(HttpRequest req, HttpResponse resp);
    protected void sendMethodNotAllowed(HttpRequest req, HttpResponse resp) {
        resp.setResponse(METHOD_NOT_SUPPORTED, "Method '%s' is not Supported".formatted(req.getMethod()));
    }
}
