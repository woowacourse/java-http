package com.techcourse;

import com.techcourse.servlet.CssServlet;
import com.techcourse.servlet.HelloWorldServlet;
import com.techcourse.servlet.IndexPageServlet;
import org.apache.catalina.startup.Tomcat;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        final var servlets = List.of(new HelloWorldServlet(), new IndexPageServlet(), new CssServlet());
        final var tomcat = new Tomcat(servlets);
        tomcat.start();
    }
}
