package com.techcourse;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Appendable.class);

    public static void main(String[] args) {
        log.info("web server start");
        Tomcat tomcat = new Tomcat();
        tomcat.start();
    }
}
