package org.apache.catalina.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.impl.DefaultServlet;
import org.apache.catalina.servlet.impl.LoginRequestServlet;
import org.apache.coyote.http11.ResponseProcessor;

public final class RequestServletContainer {

    private static final Map<String, RequestServlet> handlers = new HashMap<>();
    private static final RequestServlet defaultServlet = new DefaultServlet();

    private RequestServletContainer() {
    }

    static {
        handlers.put("/login", new LoginRequestServlet());
    }

    public static void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.startLine().path();

        handlers.getOrDefault(path, defaultServlet).handle(request, response);

        ResponseProcessor.handle(request, response);
    }
}
