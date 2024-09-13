package com.techcourse;

import org.apache.catalina.server.Server;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final Server server = new MyServer();
        final var tomcat = new Tomcat(server);
        tomcat.start();
    }
}
