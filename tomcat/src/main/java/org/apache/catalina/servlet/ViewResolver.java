package org.apache.catalina.servlet;

import org.apache.coyote.http11.Response;

public class ViewResolver {

    private static final String HTML_EXTENSION = ".html";
    private static final String RESOURCE_SEPARATOR = "/";

    public void resolve(final String viewName, final Response response) {
        response.setResourceURI(RESOURCE_SEPARATOR + viewName + HTML_EXTENSION);
    }
}
