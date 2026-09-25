package com.techcourse;

import com.techcourse.config.WebConfig;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new WebConfig().tomcat();
        tomcat.start();
    }
}
