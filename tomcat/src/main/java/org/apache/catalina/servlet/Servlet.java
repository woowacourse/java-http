package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.catalina.servlet.http.request.HttpRequest;
import org.apache.catalina.servlet.http.response.HttpResponse;

public interface Servlet {
    void service(HttpRequest request, HttpResponse response) throws IOException;
}
