package com.techcourse.web;

public interface HttpRequestHandler {
    String handle(String method, String path);
}
