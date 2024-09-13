package org.apache.catalina.servlet.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.resource.StaticResourceFinder;
import org.apache.tomcat.http.exception.MethodNotSupportException;

public class StaticResourceController extends AbstractController {

    private final String resourcePath;

    public StaticResourceController(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new MethodNotSupportException();
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final var httpResponse = StaticResourceFinder.render(resourcePath);
        response.copyProperties(httpResponse);
    }
}
