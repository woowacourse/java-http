package org.apache.catalina.servlets;

import java.io.IOException;
import org.apache.catalina.core.request.HttpRequest;
import org.apache.catalina.core.response.HttpResponse;

public abstract class HttpServlet implements Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
            return;
        }
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
        }
    }

    abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
