package org.was.controller;

import org.apache.coyote.http11.request.HttpRequest;

public interface Controller {
    ResponseResult service(HttpRequest request);
}
