package com.techcourse;

import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        var tomcat = new Tomcat();
        tomcat.start();
    }
}
