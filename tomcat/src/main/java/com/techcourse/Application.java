package com.techcourse;

import org.apache.catalina.startup.Server;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.WAS;

public class Application {
    public static void main(String[] args) {
        Server server = new Tomcat();

        new WAS(server).start();
    }
}
