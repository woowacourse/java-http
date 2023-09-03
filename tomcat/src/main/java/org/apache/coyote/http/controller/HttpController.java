package org.apache.coyote.http.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

import static org.apache.coyote.http.HttpMethod.*;

public abstract class HttpController implements Controller {

    public final void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpMethod method = httpRequest.getMethod();
        if (GET == method) {
            doGet(httpRequest, httpResponse);
        } else if (POST == method) {
            doPost(httpRequest, httpResponse);
        }
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new IllegalArgumentException();
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new IllegalArgumentException();
    }
}
