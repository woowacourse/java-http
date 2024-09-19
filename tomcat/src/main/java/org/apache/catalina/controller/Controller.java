package org.apache.catalina.controller;

import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.request.HttpRequest;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws NoSuchMethodException;
}
