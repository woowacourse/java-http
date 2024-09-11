package org.apache.catalina.servlet;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;

public interface Controller {

    boolean service(HttpRequest request, HttpResponse response);
}
