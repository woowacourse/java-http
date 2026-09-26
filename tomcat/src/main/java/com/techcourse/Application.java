package com.techcourse;

import com.techcourse.resource.StaticResourceLoader;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        StaticResourceLoader resourceLoader = new StaticResourceLoader();
        RequestMapping requestMapping = new RequestMapping(resourceLoader);
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
