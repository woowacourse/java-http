package org.apache.catalina.controller;

import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
