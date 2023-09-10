package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StandardWrapper {
    private static final Map<String, Servlet> servlets;

    static {
        servlets = new HashMap<>();
        servlets.put("default", new DefaultServlet());
        servlets.put("dispatcher", new DispatcherServlet());
    }


    public static void invoke(final HttpRequest request, final HttpResponse response) throws IOException {
        final Servlet servlet = servlets.get(request.getHttpExtension().getServletType());
        servlet.service(request,response);
    }
}
