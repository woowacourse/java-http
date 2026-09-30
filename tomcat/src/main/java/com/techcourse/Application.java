package com.techcourse;

import com.techcourse.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var requestHandler = new HttpRequestHandler(new RequestMapping());
        final var tomcat = new Tomcat(requestHandler);
        tomcat.start();
    }
}
