package org.apache.coyote.http11.message.request;

public class HttpUrl {

    private final String path;
    private final QueryParameters queryParameters;

    public HttpUrl(String path, QueryParameters queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public boolean hasQueryString() {
        return queryParameters.hasParameters();
    }

    public String getQueryParameter(String key) {
        return queryParameters.getSingleValueByKey(key);
    }

    public String getPath() {
        return path;
    }
}
