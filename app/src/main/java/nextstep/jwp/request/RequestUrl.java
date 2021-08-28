package nextstep.jwp.request;

import java.util.Objects;

public class RequestUrl {

    private static final String QUERY_STRING_SEPARATOR = "?";

    private final String requestUrl;
    private final QueryParam queryParam;

    public RequestUrl(String requestUrl, QueryParam queryParam) {
        this.requestUrl = requestUrl;
        this.queryParam = queryParam;
    }

    public static RequestUrl of(String parsedRequestUrl) {
        if (parsedRequestUrl.contains(QUERY_STRING_SEPARATOR)) {
            final int queryParamSeparator = parsedRequestUrl.lastIndexOf("?");
            final String requestUrl = parsedRequestUrl.substring(0, queryParamSeparator);
            QueryParam queryParam = QueryParam.of(parsedRequestUrl.substring(queryParamSeparator + 1));
            return new RequestUrl(requestUrl, queryParam);
        }
        return new RequestUrl(parsedRequestUrl, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestUrl that = (RequestUrl) o;
        return Objects.equals(requestUrl, that.requestUrl) &&
                Objects.equals(queryParam, that.queryParam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestUrl, queryParam);
    }
}
