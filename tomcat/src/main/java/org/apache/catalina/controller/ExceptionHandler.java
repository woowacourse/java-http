package org.apache.catalina.controller;

import org.apache.coyote.http11.response.HttpResponse;

@FunctionalInterface
public interface ExceptionHandler {

    HttpResponse handle(Exception e);
}
