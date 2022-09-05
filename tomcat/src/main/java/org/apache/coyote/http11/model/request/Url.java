package org.apache.coyote.http11.model.request;

public class Url {

    private static final int URL_INDEX = 0;
    private static final int QUERY_INDEX = 1;
    private static final String QUERY_SEPARATOR = "\\?";
    private static final int URL_WITH_QUERY_LENGTH = 2;

    private final String url;
    private final QueryParams queryParams;

    private Url(final String url, final QueryParams queryParams) {
        this.url = url;
        this.queryParams = queryParams;
    }

    public static Url of(final String url) {
        String[] split = url.split(QUERY_SEPARATOR);
        if (hasQueryString(split)) {
            return new Url(split[URL_INDEX], QueryParams.of(split[QUERY_INDEX]));
        }
        return new Url(url, QueryParams.empty());
    }

    private static boolean hasQueryString(final String[] split) {
        return split.length == URL_WITH_QUERY_LENGTH;
    }

    public String getUrl() {
        return url;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }
}
