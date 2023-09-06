package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestPath {

    private final String resource;
    private final QueryParameter queryParameter;

    private RequestPath(final String resource, final QueryParameter queryParameter) {
        this.resource = resource;
        this.queryParameter = queryParameter;
    }

    public static RequestPath from(final String uri) {
        if (!uri.contains("?")) {
            return new RequestPath(uri, QueryParameter.empty());
        }

        final int queryStringStartIndex = uri.indexOf("?");
        final String resource = uri.substring(0, queryStringStartIndex);
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
