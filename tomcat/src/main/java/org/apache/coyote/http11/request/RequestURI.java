package org.apache.coyote.http11.request;

public class RequestURI {
    private static final String QUERY_STRING_INIT_SIGN = "?";
    private static final String EXTENSION_INIT_SIGN = ".";
    private static final String HTML_EXTENSION = ".html";

    private final String uri;
    private final String resourcePath;
    private final QueryString queryString;

    private RequestURI(String uri, String resourcePath, QueryString queryString) {
        this.uri = uri;
        this.resourcePath = resourcePath;
        this.queryString = queryString;
    }

    public static RequestURI from(String uri) {
        return new RequestURI(uri, parseResourcePath(uri), QueryString.from(uri));
    }

    private static String parseResourcePath(String uri) {
        int index = uri.indexOf(QUERY_STRING_INIT_SIGN);
        if (index != -1) {
            return uri.substring(0, index) + HTML_EXTENSION;
        }
        if (!uri.contains(EXTENSION_INIT_SIGN)) {
            return uri + HTML_EXTENSION;
        }
        return uri;
    }

    public boolean hasQueryString() {
        return !queryString.getValues().isEmpty();
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public String getUri() {
        return uri;
    }

}
