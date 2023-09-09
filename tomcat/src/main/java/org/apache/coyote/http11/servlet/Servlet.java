package org.apache.coyote.http11.servlet;

import java.io.IOException;
import org.apache.coyote.http11.common.request.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.exception.MethodNotAllowedException;

public abstract class Servlet {

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }
}
