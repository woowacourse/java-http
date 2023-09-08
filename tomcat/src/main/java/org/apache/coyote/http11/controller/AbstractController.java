package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.exception.UnSupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        final HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (method == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        throw new UnSupportedHttpMethodException(method);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        throw new UnSupportedHttpMethodException(request.getMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnSupportedHttpMethodException(request.getMethod());
    }
}
