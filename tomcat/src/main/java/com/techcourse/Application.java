package com.techcourse;

import com.techcourse.servlet.mapping.ApplicationRequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        tomcat.start(new ApplicationRequestMapping());
    }
}
