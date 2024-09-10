package com.techcourse;

import com.techcourse.config.ApplicationConfig;
import org.apache.catalina.config.TomcatConfig;
import org.apache.catalina.controller.ControllerRegistry;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        ControllerRegistry.registerControllers("com.techcourse.controller");
        TomcatConfig tomcatConfig = new ApplicationConfig();
        Tomcat tomcat = new Tomcat(tomcatConfig);
        tomcat.start();
    }
}
