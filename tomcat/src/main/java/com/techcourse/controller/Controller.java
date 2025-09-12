package com.techcourse.controller;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
