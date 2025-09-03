package com.techcourse;

import com.techcourse.servlet.HelloWorldServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.static_resource.*;
import org.apache.catalina.startup.Tomcat;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        final var servlets = List.of(new LoginServlet(), new LoginPageServlet(), new HelloWorldServlet(), new IndexPageServlet(), new CssServlet(), new ChartAreaJsServlet(), new ChartBarJsServlet(), new ChartPieJsServlet(), new ScriptsJsServlet());
        final var tomcat = new Tomcat(servlets);
        tomcat.start();
    }
}
