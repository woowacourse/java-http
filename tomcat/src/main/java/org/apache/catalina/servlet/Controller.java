package org.apache.catalina.servlet;

import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response);
}
