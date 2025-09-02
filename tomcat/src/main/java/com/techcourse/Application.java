package com.techcourse;

import com.techcourse.servlet.*;
import org.apache.catalina.startup.Tomcat;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        final var servlets = List.of(new HelloWorldServlet(), new IndexPageServlet(), new CssServlet(), new ChartAreaJsServlet(), new ChartBarJsServlet(), new ChartPieJsServlet(), new ScriptsJsServlet());
        final var tomcat = new Tomcat(servlets);
        tomcat.start();
    }
}
