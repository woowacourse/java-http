package com.techcourse;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.HandlerMapping;
import org.apache.coyote.http11.RequestDispatcher;

public class Application {

    public static void main(String[] args) {
        final HandlerMapping handlerMapping = new HandlerMapping("com.techcourse.controller");
        final RequestDispatcher requestDispatcher = new RequestDispatcher(handlerMapping);
        final var tomcat = new Tomcat(requestDispatcher);
        tomcat.start();
    }
}
