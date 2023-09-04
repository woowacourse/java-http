package org.apache.coyote.http.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;

import static org.apache.coyote.http.request.HttpMethod.*;

public abstract class HttpController implements Controller {

    public final void service(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        HttpMethod method = httpRequest.getMethod();
        if (GET == method) {
            doGet(httpRequest, httpResponse, session);
        } else if (POST == method) {
            doPost(httpRequest, httpResponse, session);
        }
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        throw new IllegalArgumentException();
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        throw new IllegalArgumentException();
    }
}
