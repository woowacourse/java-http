package org.apache.coyote.http11.request.startline;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private final String requestPath;
    private final Map<String, String> queryParameters;

    public static RequestUri from(final String rawRequestUri) {
        final RequestUriParser parser = RequestUriParser.getInstance();

        final String requestPath = parser.parsePath(rawRequestUri);
        final Map<String, String> parameters = parser.parseQueryParameters(rawRequestUri);

        return new RequestUri(requestPath, parameters);
    }

    public boolean isPathEqualsTo(final String requestPath) {
        return this.requestPath.equals(requestPath);
    }

    public String getRequestPath() {
        return this.requestPath;
    }

    private RequestUri(final String requestPath, final Map<String, String> queryParameters) {
        this.requestPath = requestPath;
        this.queryParameters = new HashMap<>(queryParameters);
    }
}
