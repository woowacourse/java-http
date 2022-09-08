package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public abstract class Controller {

    public void service(HttpRequest request, HttpResponse response) {
        if (request.isMethodOf(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethodOf(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException("API not implemented");
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);
}
