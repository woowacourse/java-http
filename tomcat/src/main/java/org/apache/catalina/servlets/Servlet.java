package org.apache.catalina.servlets;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Servlet {

    void service(HttpRequest request, HttpResponse response);
}
