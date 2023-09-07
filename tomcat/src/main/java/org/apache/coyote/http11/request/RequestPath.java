package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestPath {

    private static final int BEGIN_INDEX = 0;
    private static final String DELIMITER = "?";

    private final String resource;
    private final QueryParameter queryParameter;

    private RequestPath(final String resource, final QueryParameter queryParameter) {
        this.resource = resource;
        this.queryParameter = queryParameter;
    }

    public static RequestPath from(final String uri) {
        if (!uri.contains(DELIMITER)) {
            return new RequestPath(uri, QueryParameter.empty());
        }

        final int queryStringStartIndex = uri.indexOf(DELIMITER);
        final String resource = uri.substring(BEGIN_INDEX, queryStringStartIndex);
        final String queryString = uri.substring(queryStringStartIndex + 1);

        return new RequestPath(resource, QueryParameter.from(queryString));
    }

    public boolean contains(final String extension) {
        return resource.contains(extension);
    }

    public boolean isParamEmpty() {
        return queryParameter.getParams().isEmpty();
    }

    public String getResource() {
        return resource;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter.getParams();
    }
}
