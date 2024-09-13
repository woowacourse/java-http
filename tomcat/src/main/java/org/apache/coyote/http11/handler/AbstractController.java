package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.exception.HttpNotSupportedException;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.isMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        throw new HttpNotSupportedException(request.getMethod(), request.getUrlPath());
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new HttpNotSupportedException(request.getMethod(), request.getUrlPath());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new HttpNotSupportedException(request.getMethod(), request.getUrlPath());
    }
}
