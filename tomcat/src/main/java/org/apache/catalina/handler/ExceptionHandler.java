package org.apache.catalina.handler;

import http.response.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle(Exception e);
}
