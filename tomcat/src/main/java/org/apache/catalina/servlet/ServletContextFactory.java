package org.apache.catalina.servlet;

import com.techcourse.servlet.LoginHttpServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterGetHttpServlet;
import com.techcourse.servlet.RegisterPostHttpServlet;

public class ServletContextFactory {

    private static ServletContext servletContext;

    private ServletContextFactory() {
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
        context.addServlet(new LoginServlet());
        context.addServlet(new LoginHttpServlet());
        context.addServlet(new RegisterGetHttpServlet());
        context.addServlet(new RegisterPostHttpServlet());
        servletContext = context;
    }
}
