package org.apache.coyote.http.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponseComposer;

import static org.apache.coyote.http.HttpMethod.*;

public abstract class HttpController implements Controller {

    public final void service(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        String method = httpRequest.getHeader().getMethod();
        if (GET.value.equals(method)) {
            doGet(httpRequest, httpResponseComposer);
        } else if (POST.value.equals(method)) {
            doPost(httpRequest, httpResponseComposer);
        } else if (PATCH.value.equals(method)) {
            doPatch(httpRequest, httpResponseComposer);
        }
    }

    public void doGet(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        throw new IllegalArgumentException();
    }

    public void doPost(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        throw new IllegalArgumentException();
    }

    public void doPatch(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        throw new IllegalArgumentException();
    }
}
