package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.connector.Request;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpResponse;

public abstract class MethodDispatchingController implements Controller {

    @Override
    public void handle(final Request request, final HttpResponse response) throws IOException {
        if (request.requestLine().method() == HttpMethod.GET) {
            doGet(request, response);
        } else if (request.requestLine().method() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(final Request request, final HttpResponse response) throws IOException {
    }

    protected void doPost(final Request request, final HttpResponse response) throws IOException {
    }
}
