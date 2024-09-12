package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestMethod;
import org.apache.coyote.http11.HttpResponse;

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
