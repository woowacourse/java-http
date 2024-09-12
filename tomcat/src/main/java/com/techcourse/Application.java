package com.techcourse;

import com.techcourse.config.RequestMapperConfig;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        RequestMapperConfig requestMapperConfig = new RequestMapperConfig();
        requestMapperConfig.configure();

        tomcat.start();
    }
}
