package org.apache.coyote.http11;

public class HttpRequest {
    private static final char REQUEST_URI_DELIMITER = '?';

    private final String resourcePath;
    private final QueryParameters queryParameters;

    public HttpRequest(String requestUri) {
        int delimiterIndex = requestUri.indexOf(REQUEST_URI_DELIMITER);

        if(delimiterIndex == -1) {
            this.resourcePath = requestUri;
            this.queryParameters = new QueryParameters();
            return;
        }

        this.resourcePath = requestUri.substring(0, delimiterIndex);
        this.queryParameters = new QueryParameters(requestUri.substring(delimiterIndex + 1));
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getQueryParameter(String key) {
        return queryParameters.getParameter(key);
    }
}
