package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstrcatController implements Controller {

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (GET.equals(request.getMethod())) {
            doGet(request, response);
            return;
        }
        if (POST.equals(request.getMethod())) {
            doPost(request, response);
            return;
        }
        response.methodNotAllowed();
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}
