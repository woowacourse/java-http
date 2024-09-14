package com.techcourse.resolver;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public interface Resolver {
    void doGet(HttpRequest request, HttpResponse response);

    void doPost(HttpRequest request, HttpResponse response);

    HttpResponse resolve(HttpRequest request);

    String getLocation();
}
