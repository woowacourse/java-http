package org.apache.coyote.servlet;

import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public interface Servlet {

    void service(HttpRequest request, HttpResponse response);
}
