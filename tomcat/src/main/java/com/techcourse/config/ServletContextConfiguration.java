package com.techcourse.config;

import com.techcourse.servlet.IndexRedirectServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import org.apache.catalina.servlet.ServletContext;

public class ServletContextConfiguration {

    private static ServletContext servletContext;

    private ServletContextConfiguration() {
    }

    public static ServletContext getContext() {
        if (servletContext != null) {
            return servletContext;
        }
        buildContext();
        return servletContext;
    }

    private static void buildContext() {
        ServletContext context = new ServletContext();
        context.addServlet("/", new IndexRedirectServlet());
        context.addServlet("/login", new LoginServlet());
        context.addServlet("/register", new RegisterServlet());
        servletContext = context;
    }
}
