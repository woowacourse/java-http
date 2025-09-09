package com.techcourse;

import com.techcourse.servlet.HelloWorldServlet;
import com.techcourse.servlet.HomeServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import com.techcourse.servlet.StaticResourceServlet;
import org.apache.catalina.servlet.ServletContainer;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        ServletContainer servletContainer = ServletContainer.getInstance();
        servletContainer.add("/", new HelloWorldServlet());
        servletContainer.add("/index.html", new HomeServlet());
        servletContainer.add("/login", new LoginServlet());
        servletContainer.add("/register", new RegisterServlet());
        servletContainer.setFallBackServlet(new StaticResourceServlet());
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
