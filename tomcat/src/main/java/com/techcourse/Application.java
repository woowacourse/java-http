package com.techcourse;

import com.techcourse.config.ApplicationConfiguration;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final ApplicationConfiguration configuration = new ApplicationConfiguration();
        final Tomcat tomcat = configuration.tomcat();
        tomcat.start();
    }
}
