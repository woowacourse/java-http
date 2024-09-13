package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@FunctionalInterface
public interface RequestHandler {

    void handle(HttpRequest request, HttpResponse response);
}
