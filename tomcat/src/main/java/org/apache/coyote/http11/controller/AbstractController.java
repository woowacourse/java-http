package org.apache.coyote.http11.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response, session);
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response, session);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
    }
}
