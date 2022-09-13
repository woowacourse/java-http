package org.apache.catalina.controller;


import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response);

    boolean canHandle(final HttpRequest request);
}
