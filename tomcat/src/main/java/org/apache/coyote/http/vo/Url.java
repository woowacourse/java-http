package org.apache.coyote.http.vo;

public class Url {

    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final int URL_PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private final String urlPath;
    private final QueryStrings queryStrings;

    public Url(final String urlPath, final QueryStrings queryStrings) {
        this.urlPath = urlPath;
        this.queryStrings = queryStrings;
    }

    public static Url from(final String url) {
        return new Url(parseUrlPath(url), parseQueryString(url));
    }

    private static String parseUrlPath(final String url) {
        return url.split(QUERY_STRING_DELIMITER)[URL_PATH_INDEX].trim();
    }

    private static QueryStrings parseQueryString(final String url) {
        if (url.split(QUERY_STRING_DELIMITER).length > 1) {
            return QueryStrings.from(url.split(QUERY_STRING_DELIMITER)[QUERY_STRING_INDEX]);
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

    public boolean isSameUrl(final Url url) {
        return this.urlPath.equals(url.urlPath);
    }
}
