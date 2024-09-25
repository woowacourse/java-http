package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    void requestMapping(HttpRequest request, HttpResponse httpResponse);
}
