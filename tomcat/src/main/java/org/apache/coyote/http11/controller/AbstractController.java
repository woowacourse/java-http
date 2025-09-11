package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method  = request.getMethod();
        if (method == HttpMethod.GET) {
            doGet(request, response);
        }
        if (method == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {};
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {};
}
