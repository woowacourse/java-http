package org.apache.controller;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public interface Controller {

    boolean canProcessable(HttpRequest request);

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
