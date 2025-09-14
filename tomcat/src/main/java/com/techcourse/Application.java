package com.techcourse;

import com.techcourse.util.StaticResourceManager;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        StaticResourceManager.initialize();
        
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
