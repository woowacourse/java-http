package com.techcourse;

import org.apache.catalina.startup.Tomcat;

public class Application {
    private static final int SERVER_PORT = 8080;
    private static final int SERVER_ACCEPT_ACCOUNT = 100;
    private static final int MAX_THREADS = 250;

    public static void main(String[] args) {
        final var tomcat = new Tomcat(SERVER_PORT, SERVER_ACCEPT_ACCOUNT, MAX_THREADS);
        tomcat.start();
    }
}
