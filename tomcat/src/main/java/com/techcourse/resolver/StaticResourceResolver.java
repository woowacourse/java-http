package com.techcourse.resolver;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


public class StaticResourceResolver implements Resolver {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String body = new ResourceFinder(request.getLocation()).getStaticResource(response);
        response.setBody(body);
    }

    @Override
    public HttpResponse resolve(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        doGet(request, response);
        return response;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        throw new IllegalStateException();
    }

    @Override
    public String getLocation() {
        throw new IllegalStateException();
    }
}
