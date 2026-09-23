package com.techcourse;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.RequestMapping;

public class Application {

    public static void main(String[] args) {
        final var requestMapping = new RequestMapping();
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
