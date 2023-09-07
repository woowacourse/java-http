package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestUri {

    private final String requestUri;

    public RequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public boolean contains(final String uri) {
        return requestUri.contains(uri);
    }

    public boolean endsWith(final String uri) {
        return requestUri.endsWith(uri);
    }

    public QueryString getQueryString() {
        final String[] parsedRequestUri = requestUri.split("\\?");

        final Map<String, String> queryStrings = Arrays.stream(parsedRequestUri[1].split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        return new QueryString(queryStrings);
    }

    public String getRequestUri() {
        return requestUri;
    }
}
