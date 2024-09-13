package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest httpRequest);

    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
