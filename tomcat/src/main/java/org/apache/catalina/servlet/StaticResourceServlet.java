package org.apache.catalina.servlet;

import java.net.URI;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceServer;

public class StaticResourceServlet implements HttpServlet {

    private static final StaticResourceServlet instance = new StaticResourceServlet();

    private StaticResourceServlet() {
    }

    public static StaticResourceServlet getInstance() {
        return instance;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        URI uri = request.getUri();
        StaticResourceServer.load(response, uri.getPath());
    }
}
