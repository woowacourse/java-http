package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestURI {
    private final QueryStringParser queryStringParser;
    private final String uri;
    private final Map<String, String> parameters;

    public RequestURI(String requestUri) {
        this.queryStringParser = new QueryStringParser();
        this.uri = parsedUri(requestUri);
        this.parameters = parsedParameters(requestUri);
    }

    private String parsedUri(String requestUri) {
        if (queryStringParser.isQueryString(requestUri)) {
            return queryStringParser.parseUri(requestUri);
        }
        return requestUri;
    }

    private Map<String, String> parsedParameters(String requestUri) {
        if (queryStringParser.isQueryString(requestUri)) {
            return queryStringParser.parseParameters(requestUri);
        }
        return new HashMap<>();
    }

    public boolean isQueryStringUri() {
        return queryStringParser.isQueryString(uri);
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
