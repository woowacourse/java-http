package org.apache.catalina.controller;

import org.apache.catalina.connector.Request;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {
    void handle(Request request, HttpResponse response) throws Exception;
}
