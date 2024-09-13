package org.apache.catalina.controller;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Controller {

    boolean canHandleRequest(HttpRequest request);
    void service(HttpRequest request, HttpResponse response);
}
