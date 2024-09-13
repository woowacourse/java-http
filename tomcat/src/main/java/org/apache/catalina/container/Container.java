package org.apache.catalina.container;

import org.apache.catalina.ExceptionHandler;
import org.apache.catalina.HandlerMapping;

public interface Container {

    HandlerMapping getHandlerMapping();

    ExceptionHandler getExceptionHandler();
}
