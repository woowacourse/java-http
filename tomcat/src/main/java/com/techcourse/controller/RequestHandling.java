package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface RequestHandling {
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
