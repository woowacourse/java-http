package org.apache.catalina.servlet;

import java.net.URI;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceLoader;

public class StaticResourceServlet implements HttpServlet {

    private static final StaticResourceServlet instance = new StaticResourceServlet();

    private StaticResourceServlet() {
    }

    public static StaticResourceServlet getInstance() {
        return instance;
    }

    @Override
    public boolean canService(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        // TODO: 404 on ResourceNotFound
        URI uri = request.getUri();
        byte[] body = StaticResourceLoader.load(uri.getPath());
        response.ok(body);
    }
}
