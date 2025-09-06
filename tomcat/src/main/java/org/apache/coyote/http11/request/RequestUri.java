package org.apache.coyote.http11.request;

record RequestUri(
        String uri,
        String path,
        String queryString) {

    private static final String QUERY_SEPARATOR = "?";

    public static RequestUri from(final String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            throw new IllegalArgumentException("URI가 누락 되었습니다.");
        }

        int queryIndex = uri.indexOf(QUERY_SEPARATOR);
        if (queryIndex == -1) {
            return new RequestUri(uri, uri, null);
        }

        String path = uri.substring(0, queryIndex);
        String queryString = uri.substring(queryIndex + 1);
        return new RequestUri(uri, path, queryString);
    }

    String getPath() {
        return path;
    }

    String getQueryString() {
        return queryString;
    }
}
