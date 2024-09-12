package org.apache.catalina.servlet;

import org.apache.catalina.connector.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle();
}
