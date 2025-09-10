package com.techcourse;

import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import com.techcourse.servlet.StaticResourceServlet;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        final var container = tomcat.getServletContainer();
        container.addServlet("/login", new LoginServlet());
        container.addServlet("/register", new RegisterServlet());
        container.addServlet("/*", new StaticResourceServlet());

        tomcat.start();
    }
}
