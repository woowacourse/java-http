package com.techcourse.config;

import com.techcourse.exception.GlobalExceptionHandler;
import org.apache.catalina.config.DefaultTomcatConfig;
import org.apache.catalina.controller.ExceptionHandler;

public class ApplicationConfig extends DefaultTomcatConfig {

    private static final ExceptionHandler EXCEPTION_HANDLER = new GlobalExceptionHandler();

    @Override
    public ExceptionHandler exceptionHandler() {
        return EXCEPTION_HANDLER;
    }
}
