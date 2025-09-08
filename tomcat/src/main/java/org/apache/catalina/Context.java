package org.apache.catalina;

public class Context {

    public ServletContainer createServletContainer() {
        return new ServletContainer();
    }
}
