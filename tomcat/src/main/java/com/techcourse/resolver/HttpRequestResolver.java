package com.techcourse.resolver;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpRequestResolver implements Resolver {
    private final String location;

    public HttpRequestResolver() {
        this.location = this.getClass().getAnnotation(Location.class).value();
    }

    public void doGet(HttpRequest request, HttpResponse response) {
//        response.toMethodNotAllowed();
    }

    public void doPost(HttpRequest request, HttpResponse response) {
//        response.toMethodNotAllowed();
    }

    public HttpResponse resolve(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
        return response;
    }

    @Override
    public final String getLocation() {
        return location;
    }
}
