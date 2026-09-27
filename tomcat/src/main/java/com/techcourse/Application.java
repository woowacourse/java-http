package com.techcourse;

import com.techcourse.config.ControllerConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;

public class Application {

    public static void main(String[] args) {
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
