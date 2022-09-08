package org.apache.catalina.servlet;

import org.apache.coyote.HttpResponse;
import org.apache.coyote.support.HttpException;

public abstract class ExceptionHandler {

    public abstract void handle(HttpException exception, HttpResponse response);
}
