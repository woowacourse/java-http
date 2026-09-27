package com.techcourse;

import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();

        final Tomcat tomcat = new Tomcat(config.container());
        tomcat.start();
    }
}
