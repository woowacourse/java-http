package org.apache.coyote.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseInfo;

public abstract class RequestHandler {

    final ClassLoader classLoader = getClass().getClassLoader();
    String mappingUri;

    public static final String RESOURCE_PATH = "static";
    public static final String PAGE401 = "/401.html";
    public static final String HTTP_FOUND = "Found";
    public static final String INDEX_PAGE = "/index.html";

    public boolean isMatch(final HttpRequest httpRequest) {
        final String requestURI = httpRequest.getRequestLine().getRequestURI();
        return mappingUri.equals(requestURI);
    }

    public abstract ResponseInfo doService(HttpRequest httpRequest);

    abstract ResponseInfo doGet(HttpRequest httpRequest);

    abstract ResponseInfo doPost(HttpRequest httpRequest);
}
