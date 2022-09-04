package org.apache.coyote.http11;

public class RequestUri {

    private static final String STATIC_FILE_PATH = "static";

    private final String resourcePath;
    private final QueryParameters queryParams;

    private RequestUri(final String resourcePath, final QueryParameters queryParams) {
        this.resourcePath = resourcePath;
        this.queryParams = queryParams;
    }

    public static RequestUri of(final String uri) {
        return new RequestUri(parseResourcePath(uri), QueryParameters.of(uri));
    }

    private static String parseResourcePath(String uri) {
        int index = uri.indexOf("?");
        if (index != -1) {
            return STATIC_FILE_PATH + uri.substring(0, index) + ".html";
        }
        return STATIC_FILE_PATH + uri;
    }

    public boolean isResourceFileRequest() {
        return !resourcePath.equals(STATIC_FILE_PATH + "/");
    }

    public boolean hasQueryParams() {
        return !queryParams.isEmpty();
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public QueryParameters getQueryParams() {
        return queryParams;
    }
}
