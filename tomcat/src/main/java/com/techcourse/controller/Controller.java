package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    HttpResponse handle(HttpRequest httpRequest);

    String getPath();

    HttpMethod getMethod();
}
