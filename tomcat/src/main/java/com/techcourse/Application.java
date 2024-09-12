package com.techcourse;

import java.util.Map;

import org.apache.catalina.servlet.PathMatchServletContainer;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = new RequestMapping(
                Map.of()
        );
        PathMatchServletContainer servletContainer = new PathMatchServletContainer(requestMapping);
        var tomcat = new Tomcat(servletContainer);
        tomcat.start();
    }
}
