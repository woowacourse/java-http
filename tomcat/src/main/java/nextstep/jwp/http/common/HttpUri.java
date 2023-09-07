package nextstep.jwp.http.common;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.request.QueryString;

public class HttpUri {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final int QUERY_STRING_NOT_EXIST_INDEX = -1;
    private final String nativePath;
    private final QueryString queryString;

    private HttpUri(String nativePath, QueryString queryString) {
        this.nativePath = nativePath;
        this.queryString = queryString;
    }

    private HttpUri(String nativePath) {
        this(nativePath, QueryString.from(""));
    }

    public static HttpUri from(String fullPath) {
        if (fullPath == null) {
            throw new BadRequestException("HttpUri fullPath is Null");
        }

        int index = fullPath.indexOf(QUERY_STRING_DELIMITER);

        if (index == QUERY_STRING_NOT_EXIST_INDEX) {
            return new HttpUri(fullPath);
        }

        String nativePath = fullPath.substring(0, index);
        QueryString queryString = QueryString.from(fullPath.substring(index + 1));

        return new HttpUri(nativePath, queryString);
    }

    public boolean hasQueryString() {
        return queryString.hasValues();
    }

    public String getNativePath() {
        return nativePath;
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
