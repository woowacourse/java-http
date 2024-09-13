package com.techcourse;

import org.apache.catalina.servlet.PathMatchServletContainer;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.startup.Tomcat;

import com.techcourse.config.RequestMappingConfig;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = RequestMappingConfig.getRequestMapping();
        PathMatchServletContainer servletContainer = new PathMatchServletContainer(requestMapping);
        var tomcat = new Tomcat(servletContainer);
        tomcat.start();
    }
}
