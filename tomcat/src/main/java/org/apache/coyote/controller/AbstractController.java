package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.value.HttpMethod;

public abstract class AbstractController implements Controller {

    private final String targetUrl;

    public AbstractController(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public boolean canProcessable(HttpRequest request) {
        return request.getUri().equals(targetUrl);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        } else if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
