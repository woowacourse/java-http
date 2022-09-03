package org.apache.coyote.http11;

public class RequestURI {

    private final String path;
    private final QueryParameters queryParameters;

    public RequestURI(final String requestURI) {
        if (!requestURI.contains("?")) {
            this.path = requestURI;
            this.queryParameters = QueryParameters.EMPTY_QUERY_PARAMETERS;
            return;
        }

        int index = requestURI.indexOf("?");
        this.path = requestURI.substring(0, index);
        this.queryParameters = new QueryParameters(requestURI.substring(index + 1));
    }

    public boolean isQueryParametersEmpty() {
        return queryParameters.isEmpty();
    }

    public String getQueryParameterKey(final String key) {
        return queryParameters.findByQueryParameterKey(key);
    }

    public String getPath() {
        return path;
    }
}
