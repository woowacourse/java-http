package org.apache.catalina.servlet;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public interface Servlet {
    void service(HttpRequest request, HttpResponse response);
}
