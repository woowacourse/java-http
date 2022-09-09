package org.apache.catalina.servlet;

import static org.apache.coyote.http11.util.HttpMethod.POST;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpMethod;

public class AbstractServlet implements Servlet {
    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod == POST) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
