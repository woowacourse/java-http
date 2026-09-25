package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
