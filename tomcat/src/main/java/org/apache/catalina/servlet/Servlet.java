package org.apache.catalina.servlet;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

//TODO: jakarta.servlet.Servlet과 비교하기  (2025-09-8, 월, 0:8)
public interface Servlet {
    void service(HttpRequest request, HttpResponse response);
}
