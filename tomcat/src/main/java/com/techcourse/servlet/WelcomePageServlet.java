package com.techcourse.servlet;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class WelcomePageServlet extends HttpServlet {

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.isMethod(HttpMethod.GET)) {
            doGet(req, resp);
        } else {
            sendMethodNotAllowed(req, resp);
        }
    }

    @Override
    protected void doGet(HttpRequest req, HttpResponse resp) {
        resp.setResponse("200 OK", "Hello world!");
    }

    @Override
    protected void doPost(HttpRequest req, HttpResponse resp) {
    }

    @Override
    protected void doPut(HttpRequest req, HttpResponse resp) {
    }

    @Override
    protected void doDelete(HttpRequest req, HttpResponse resp) {
    }

    @Override
    protected void doPatch(HttpRequest req, HttpResponse resp) {
    }
}
