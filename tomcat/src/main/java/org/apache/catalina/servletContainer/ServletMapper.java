package org.apache.catalina.servletContainer;

import java.util.List;
import java.util.Optional;
import org.apache.catalina.servlet.DefaultServlet;
import org.apache.catalina.servlet.LoginServlet;
import org.apache.catalina.servlet.RegisterServlet;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.StaticResourceServlet;
import org.apache.coyote.request.HttpRequest;

public class ServletMapper {

    private final List<Servlet> servlets;

    public ServletMapper() {
        this.servlets = initServlets();
    }

    public Optional<Servlet> findServlet(final HttpRequest httpRequest) {
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(httpRequest))
                .findFirst();
    }

    private List<Servlet> initServlets() {
        return List.of(
                new DefaultServlet(),
                new StaticResourceServlet(),
                new LoginServlet(),
                new RegisterServlet()
        );
    }
}
