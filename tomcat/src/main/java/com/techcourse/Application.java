package com.techcourse;

import org.apache.catalina.server.Context;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final Context context = new MyContext();
        final Tomcat tomcat = new Tomcat(context);
        tomcat.start();
    }
}
