package com.techcourse;

import com.techcourse.servlet.DispatcherServlet;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(new DispatcherServlet());
        tomcat.start();
    }
}
