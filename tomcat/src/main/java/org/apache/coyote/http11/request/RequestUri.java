package org.apache.coyote.http11.request;

public class RequestUri {

    private final String path;
    private final QueryStrings queryStrings;

    private RequestUri(String path, QueryStrings queryStrings) {
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static RequestUri from(String requestUri) {
        int index = requestUri.indexOf("?");

        if (index == -1) {
            return new RequestUri(requestUri, QueryStrings.EMPTY);
        }

        String path = requestUri.substring(0, index);
        QueryStrings queryStrings = QueryStrings.from(requestUri.substring(index + 1));

        return new RequestUri(path, queryStrings);
    }

    public boolean isStaticResource() {
        return path.contains(".");
    }

    public boolean isQueryStringExisted() {
        return queryStrings.isExisted();
    }

    public String findQueryStringValue(String key) {
        return queryStrings.findValue(key);
    }

    public String getPath() {
        return path;
    }

}
