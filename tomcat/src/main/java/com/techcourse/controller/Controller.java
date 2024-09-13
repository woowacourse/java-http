package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface Controller {
    HttpResponse handle(HttpRequest request);
}
