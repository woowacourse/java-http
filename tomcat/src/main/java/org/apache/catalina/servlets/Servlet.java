package org.apache.catalina.servlets;

import java.io.IOException;
import org.apache.catalina.core.request.HttpRequest;
import org.apache.catalina.core.response.HttpResponse;

public interface Servlet {
    void service(HttpRequest request, HttpResponse response) throws IOException;
}
