package org.apache.catalina.handler;

import http.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle(Exception e);
}
