package org.apache.catalina.container.controller;

import org.apache.catalina.container.http.request.HttpRequest;
import org.apache.catalina.container.http.response.HttpResponse;

public interface Controller {

    boolean canProcessable(HttpRequest request);

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
