package org.apache.coyote.http11;

public class HttpRequest {

    private static final char QUERY_STRING_STANDARD = '?';
    private static final int INDEX_NOT_FOUND = -1;

    private final String url;
    private final QueryStrings queryStrings;

    public HttpRequest(final String uri) {
        url = extractURL(uri);
        queryStrings = new QueryStrings(uri);
    }

    private String extractURL(final String uri) {
        int index = uri.lastIndexOf(QUERY_STRING_STANDARD);
        if (index == INDEX_NOT_FOUND) {
            return uri;
        }
        return uri.substring(0, index);
    }

    public boolean isQueryStringEmpty() {
        return queryStrings.isEmpty();
    }

    public String getUrl() {
        return url;
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }
}
