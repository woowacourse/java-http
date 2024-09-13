package com.techcourse.servlet;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.resource.ResourceParser;

public class IndexServlet extends HttpServlet {

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
        resp.setResponse("200 OK", ResourceParser.getRequestFile("/index.html"));
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
