package com.techcourse;

import com.techcourse.exception.GlobalExceptionHandler;
import org.apache.catalina.controller.ControllerRegistry;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        ControllerRegistry.registerControllers("com.techcourse.controller");
        Tomcat tomcat = new Tomcat();
        tomcat.start(new GlobalExceptionHandler());
    }
}
