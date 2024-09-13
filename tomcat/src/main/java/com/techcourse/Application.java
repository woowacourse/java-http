package com.techcourse;

import com.techcourse.config.RequestMapperConfig;
import org.apache.catalina.startup.Tomcat;

public class Application {

    private static final int MAX_THREAD_COUNT = 500;

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        RequestMapperConfig requestMapperConfig = new RequestMapperConfig();
        requestMapperConfig.configure();

        tomcat.start(MAX_THREAD_COUNT);
    }
}
