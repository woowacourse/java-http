package nextstep.jwp.framework.request.details;

import java.util.Objects;

public class RequestUrl {

    private static final String QUERY_STRING_SEPARATOR = "?";

    private final String url;
    private final QueryParameter queryParameter;

    public RequestUrl(String url, QueryParameter queryParameter) {
        this.url = url;
        this.queryParameter = queryParameter;
    }

    public static RequestUrl of(String parsedRequestUrl) {
        if (parsedRequestUrl.contains(QUERY_STRING_SEPARATOR)) {
            final int queryParamSeparator = parsedRequestUrl.lastIndexOf(QUERY_STRING_SEPARATOR);
            final String requestUrl = parsedRequestUrl.substring(0, queryParamSeparator);
            final QueryParameter queryParameter = QueryParameter.of(parsedRequestUrl.substring(queryParamSeparator + 1));
            return new RequestUrl(requestUrl, queryParameter);
        }
        return new RequestUrl(parsedRequestUrl, null);
    }

    public QueryParameter getQueryParam() {
        return queryParameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestUrl that = (RequestUrl) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
