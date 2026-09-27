package com.techcourse;

import com.techcourse.controller.FrontController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        FrontController frontController = new FrontController();
        final var tomcat = new Tomcat(frontController);
        tomcat.start();
    }
}
