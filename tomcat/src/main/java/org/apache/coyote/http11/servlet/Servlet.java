package org.apache.coyote.http11.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Servlet {

    boolean canService(HttpRequest request);

    void service(HttpRequest request, HttpResponse response);
}
