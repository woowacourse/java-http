package org.apache.catalina.config;

import java.util.Map;
import org.apache.catalina.controller.DefaultExceptionHandler;
import org.apache.catalina.controller.ExceptionHandler;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionGenerator;
import org.apache.catalina.session.UuidSessionGenerator;

public class DefaultTomcatConfig implements TomcatConfig {

    private static final SessionGenerator SESSION_GENERATOR = new UuidSessionGenerator();
    private static final ExceptionHandler EXCEPTION_HANDLER = new DefaultExceptionHandler();
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping(Map.of());

    @Override
    public SessionGenerator sessionGenerator() {
        return SESSION_GENERATOR;
    }

    @Override
    public ExceptionHandler exceptionHandler() {
        return EXCEPTION_HANDLER;
    }

    @Override
    public RequestMapping requestMapping() {
        return REQUEST_MAPPING;
    }
}
