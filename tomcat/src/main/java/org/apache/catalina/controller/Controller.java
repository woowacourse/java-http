package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    boolean isMappedController(HttpRequest request);
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
