package org.apache.catalina.servlets;

import java.io.IOException;
import org.apache.catalina.servlets.http.request.HttpMethod;
import org.apache.catalina.servlets.http.request.HttpRequest;
import org.apache.catalina.servlets.http.response.HttpResponse;

public abstract class HttpServlet implements Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
