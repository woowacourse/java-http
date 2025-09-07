package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class StartLine {
    private static final String RESOURCE_PATH = "static";
    private static final String ROUTE_PATH = "/";

    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;

    public StartLine(final HttpMethod httpMethod, final String uri, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public boolean isStatic() {
        if (uri.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$")) {
            return true;
        }

        return httpMethod == HttpMethod.GET && !uri.contains("?");
    }

    public String extractPath() {
        if (uri.equals(ROUTE_PATH)) {
            return uri;
        }

        String path = uri;
        if (!path.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$")) {
            path += ".html";
        }

        return RESOURCE_PATH + path;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
