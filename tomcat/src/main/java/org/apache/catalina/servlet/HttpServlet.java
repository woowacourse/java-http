package org.apache.catalina.servlet;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.line.HttpStatus;

public abstract class HttpServlet implements Servlet {

    private static final String METHOD_NOT_ALLOWED_PAGE = "static/405.html";

    @Override
    public void doService(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED_PAGE);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED_PAGE);
    }
}
