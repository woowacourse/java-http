package com.techcourse.controller;

import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
    boolean canHandle(HttpRequest request);
}
