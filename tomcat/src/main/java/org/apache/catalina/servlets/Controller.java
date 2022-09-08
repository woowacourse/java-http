package org.apache.catalina.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    boolean isProcessable(HttpRequest request);

    void service(HttpRequest request, HttpResponse response);
}
