package org.apache.catalina.servlet.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.resource.StaticResourceFinder;
import org.apache.tomcat.http.exception.MethodNotSupportException;

public class HomeController extends AbstractController {

    private static final String HTML_PATH = "index.html";

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new MethodNotSupportException();
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final var httpResponse = StaticResourceFinder.render(HTML_PATH);
        response.copyProperties(httpResponse);
    }
}
