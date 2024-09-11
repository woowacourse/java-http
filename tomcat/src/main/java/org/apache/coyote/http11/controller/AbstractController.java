package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isPostMethod()) {
            doPost(request, response);
            return;
        }
        if (request.isGetMethod()) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
