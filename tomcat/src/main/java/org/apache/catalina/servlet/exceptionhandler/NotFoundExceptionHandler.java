package org.apache.catalina.servlet.exceptionhandler;

import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.resource.StaticResourceFinder;
import org.apache.catalina.servlet.ExceptionHandler;

public class NotFoundExceptionHandler implements ExceptionHandler {

    private static final String NOT_FOUND_HTML = "404.html";

    @Override
    public HttpResponse handle() {
        return StaticResourceFinder.render(NOT_FOUND_HTML);
    }
}
