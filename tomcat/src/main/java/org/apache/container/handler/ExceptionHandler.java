package org.apache.container.handler;

import http.response.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle(Exception e);
}
