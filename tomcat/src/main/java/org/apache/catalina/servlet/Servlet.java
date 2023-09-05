package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Servlet {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
