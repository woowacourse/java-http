package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Servlet {
    void service(final HttpRequest request, final HttpResponse response)throws IOException;
}
