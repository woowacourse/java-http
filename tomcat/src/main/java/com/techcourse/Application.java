package com.techcourse;

import org.apache.catalina.session.SessionManager;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var sessionManager = new SessionManager();
        final var tomcat = new Tomcat(sessionManager);
        tomcat.start();
    }
}
