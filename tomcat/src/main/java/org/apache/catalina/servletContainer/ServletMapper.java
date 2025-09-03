package org.apache.catalina.servletContainer;

import java.util.List;
import org.apache.catalina.servlet.DefaultServlet;
import org.apache.catalina.servlet.LoginServlet;
import org.apache.catalina.servlet.StaticResourceServlet;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.request.HttpRequest;

public class ServletMapper {

    private final List<Servlet> servlets;

    public ServletMapper() {
        this.servlets = initServlets();
    }

    public Servlet findServlet(final HttpRequest httpRequest) {
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(httpRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 처리할 수 없는 요청입니다."));
    }

    private List<Servlet> initServlets() {
        return List.of(
                new DefaultServlet(),
                new StaticResourceServlet(),
                new LoginServlet()
        );
    }
}
