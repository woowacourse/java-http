package org.apache.catalina.config;

import org.apache.catalina.controller.ExceptionHandler;
import org.apache.catalina.session.SessionGenerator;

public interface TomcatConfig {

    SessionGenerator sessionGenerator();

    ExceptionHandler exceptionHandler();
}
