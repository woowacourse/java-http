package com.techcourse;

import com.techcourse.controller.ApplicationAdapter;
import com.techcourse.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(new ApplicationAdapter(new RequestMapping()));
        tomcat.start();
    }
}
