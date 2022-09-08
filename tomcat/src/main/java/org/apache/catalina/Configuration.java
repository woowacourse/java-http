package org.apache.catalina;

public interface Configuration {

    void addHandlers();

    RequestMapper getRequestMapper();

    ExceptionHandler getExceptionHandler();
}
