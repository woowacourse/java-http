package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String DEFAULT_VALUE = "";

    private final HttpMethod method;
    private final String requestUri;
    private final QueryStrings queryStrings;
    private final HttpHeaders headers;

    public HttpRequest(final HttpMethod method, final String requestUri, final QueryStrings queryStrings,
                       final HttpHeaders headers) {
        this.method = method;
        this.requestUri = requestUri;
        this.queryStrings = queryStrings;
        this.headers = headers;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getContentType() {
        return headers.getContentType();
    }

    public String getParameter(final String key) {
        final Map<String, String> parameters = queryStrings.getQueryStrings();
        return parameters.getOrDefault(key, DEFAULT_VALUE);
    }
}
