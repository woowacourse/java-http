package org.apache.catalina.mvc.controller;


import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public interface Controller {
    HttpResponse handleRequest(HttpRequest request);
}
