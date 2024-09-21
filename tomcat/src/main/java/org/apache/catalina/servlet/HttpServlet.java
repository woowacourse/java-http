package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpServlet implements Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestMethod method = request.getMethod();

        if (method.isGet()) {
            doGet(request, response);
        } else if (method.isPost()) {
            doPost(request, response);
        } else {
            response.sendRedirect("/500.html");
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.sendRedirect("/500.html");
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendRedirect("/500.html");
    }
}
