package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;

public interface Controller {

    void service(HttpRequest req);
}
