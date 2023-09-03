package org.apache.coyote.http.vo;

public class Url {

    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final int URL_PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private final String urlPath;
    private final QueryStrings queryStrings;

    public Url(final String url) {
        this.urlPath = parseUrlPath(url);
        this.queryStrings = parseQueryString(url);
    }

    private String parseUrlPath(final String url) {
        return url.split(QUERY_STRING_DELIMITER)[URL_PATH_INDEX];
    }

    private QueryStrings parseQueryString(final String url) {
        if (url.split(QUERY_STRING_DELIMITER).length > 1) {
            return new QueryStrings(url.split(QUERY_STRING_DELIMITER)[QUERY_STRING_INDEX]);
        }
        return QueryStrings.getEmptyQueryStrings();
    }

    public boolean isContainSubString(final String subString) {
        return this.urlPath.contains(subString);
    }

    public String getUrlPath() {
        return urlPath;
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }
}
