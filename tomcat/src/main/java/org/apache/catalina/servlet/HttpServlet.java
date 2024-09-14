package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class HttpServlet implements Servlet {

    @Override
    public void doService(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
