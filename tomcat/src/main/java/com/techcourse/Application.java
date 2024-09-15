package com.techcourse;

import org.apache.catalina.server.ApplicationContext;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final ApplicationContext applicationContext = new MyApplicationContext();
        final var tomcat = new Tomcat(applicationContext);
        tomcat.start();
    }
}
