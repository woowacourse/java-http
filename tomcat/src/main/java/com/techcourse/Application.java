package com.techcourse;

import com.techcourse.config.ControllerConfig;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(ControllerConfig::forSession);
        tomcat.start();
    }
}
