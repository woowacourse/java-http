package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        final String method = request.getMethod();

        if (GET.equals(method)) {
            doGet(request, response);
            return;
        }

        if (POST.equals(method)) {
            doPost(request, response);
            return;
        }
    }

    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {

    }

    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {

    }
}
