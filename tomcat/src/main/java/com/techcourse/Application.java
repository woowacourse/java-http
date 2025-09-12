package com.techcourse;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.SessionManager;

public class Application {

    public static void main(String[] args) {
        final var sessionManager = new SessionManager();
        final var tomcat = new Tomcat(sessionManager);
        tomcat.start();
    }
}
