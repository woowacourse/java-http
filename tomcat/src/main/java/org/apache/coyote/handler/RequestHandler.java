package org.apache.coyote.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;

public abstract class RequestHandler {

    final ClassLoader classLoader = getClass().getClassLoader();
    String mappingUri;

    public static final String RESOURCE_PATH = "static";
    public static final String UNAUTHORIZED_PAGE = "/401.html";
    public static final String NOT_FOUND_PAGE = "/404.html";
    public static final String HTTP_FOUND = "Found";
    public static final String INDEX_PAGE = "/index.html";

    public boolean isMatch(final HttpRequest httpRequest) {
        final String requestURI = httpRequest.getRequestLine().getRequestURI();
        return mappingUri.equals(requestURI);
    }

    public abstract Http11Response doService(HttpRequest httpRequest);

    abstract Http11Response doGet(HttpRequest httpRequest);

    abstract Http11Response doPost(HttpRequest httpRequest);
}
