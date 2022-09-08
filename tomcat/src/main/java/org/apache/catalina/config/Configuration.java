package org.apache.catalina.config;

import org.apache.catalina.handler.ExceptionHandler;
import org.apache.catalina.handler.RequestMapper;

public interface Configuration {

    void addHandlers();

    RequestMapper getRequestMapper();

    ExceptionHandler getExceptionHandler();
}
