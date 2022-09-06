package org.apache.coyote.http11;

public class RequestUri {

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
            return uri.substring(0, index);
        }
        return uri;
    }

    public boolean hasQueryParams() {
        return !queryParams.isEmpty();
    }

    public MediaType findMediaType() {
        return MediaType.of(resourcePath);
    }

    public String parseFullPath() {
        if (resourcePath.endsWith(findMediaType().getExtension())) {
            return resourcePath;
        }
        return findMediaType().appendExtension(resourcePath);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public QueryParameters getQueryParams() {
        return queryParams;
    }
}
