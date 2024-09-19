package com.techcourse.servlet;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.resource.ResourceParser;

public class IndexServlet extends HttpServlet {

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.isMethod(HttpMethod.GET)) {
            doGet(req, resp);
        } else if(req.isMethod(HttpMethod.POST)) {
            doPost(req, resp);
        } else if(req.isMethod(HttpMethod.PUT)) {
            doPut(req, resp);
        } else if(req.isMethod(HttpMethod.DELETE)) {
            doDelete(req, resp);
        } else if(req.isMethod(HttpMethod.PATCH)) {
            doPatch(req, resp);
        }
    }

    @Override
    protected void doGet(HttpRequest req, HttpResponse resp) {
        resp.setResponse(HttpStatus.OK, ResourceParser.getRequestFile("/index.html"));
    }

    @Override
    protected void doPost(HttpRequest req, HttpResponse resp) {
        resp.setNotFound();
    }

    @Override
    protected void doPut(HttpRequest req, HttpResponse resp) {
        resp.setNotFound();
    }

    @Override
    protected void doDelete(HttpRequest req, HttpResponse resp) {
        resp.setNotFound();
    }

    @Override
    protected void doPatch(HttpRequest req, HttpResponse resp) {
        resp.setNotFound();
    }
}
