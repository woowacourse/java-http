package org.apache.catalina.servlet;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Servlet {
    void service(HttpRequest request, HttpResponse response);
}
