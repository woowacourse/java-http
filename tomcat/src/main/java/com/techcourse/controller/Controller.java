package com.techcourse.controller;


import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response);
}
