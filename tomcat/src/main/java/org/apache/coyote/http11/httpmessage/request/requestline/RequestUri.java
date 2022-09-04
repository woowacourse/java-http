package org.apache.coyote.http11.httpmessage.request.requestline;

public class RequestUri {

    private final String resourcePath;
    private final UriType uriType;
    private QueryStrings queryStrings;

    public RequestUri(String resourcePath, UriType uriType) {
        this.resourcePath = resourcePath;
        this.uriType = uriType;
    }

    public RequestUri(String resourcePath, UriType uriType,
                      QueryStrings queryStrings) {
        this.resourcePath = resourcePath;
        this.uriType = uriType;
        this.queryStrings = queryStrings;
    }

    public static RequestUri from(final String resourcePath) {
        final UriType uriType = UriType.findByResourcePath(resourcePath);

        if (uriType.equals(UriType.HAS_QUERYSTRING)) {
            final String newResourcePath = resourcePath.split("\\?")[0];
            return new RequestUri(newResourcePath, uriType, new QueryStrings(resourcePath));
        }
        return new RequestUri(resourcePath, uriType);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }

    public boolean isFileRequest() {
        return uriType == UriType.FILE_REQUEST;
    }

    public boolean hasQueryStrings() {
        return uriType == UriType.HAS_QUERYSTRING;
    }

    public boolean isMatchUri(final String requestUri) {
        return resourcePath.equals(requestUri);
    }
}
