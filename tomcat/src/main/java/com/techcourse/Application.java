package com.techcourse;

import com.techcourse.web.resource.StaticResourceHandler;
import com.techcourse.web.routing.TechCourseRequestMapping;
import org.apache.catalina.connector.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    private static final RequestMapping requestMapping = new TechCourseRequestMapping(new StaticResourceHandler());

    public static void main(String[] args) {
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
