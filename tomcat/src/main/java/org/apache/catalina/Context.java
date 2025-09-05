package org.apache.catalina;

import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.StaticResourceServlet;

public class Context {

    public ServletContainer createServletContainer() {
        final ServletContainer container = new ServletContainer();

        container.addServlet("/login", new LoginServlet());
        container.addServlet("/*", new StaticResourceServlet());

        return container;
    }
}
