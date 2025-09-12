package org.apache.catalina.core.controller;

import org.apache.tomcat.util.http.request.HttpRequest;
import org.apache.tomcat.util.http.response.HttpResponse;

public interface Controller {

    boolean canProcessable(HttpRequest request);

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
