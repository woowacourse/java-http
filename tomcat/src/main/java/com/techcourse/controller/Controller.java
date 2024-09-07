package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Controller {


    Logger log = LoggerFactory.getLogger(Controller.class);

    void service(HttpRequest request, HttpResponse response);
}
