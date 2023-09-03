package org.apache;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("웹 서버 start.");
        final Tomcat tomcat = new Tomcat();
        tomcat.start();
    }

}
