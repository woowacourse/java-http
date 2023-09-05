package org.apache.catalina.servlet;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isMatchMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isMatchMethod(HttpMethod.POST)) {
            doPost(request, response);
        }

    }

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected abstract void doGet(HttpRequest request, HttpResponse response);
}
