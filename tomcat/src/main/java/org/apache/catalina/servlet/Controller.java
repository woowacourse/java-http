package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response);
}
