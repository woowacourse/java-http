package nextstep.jwp.framework.http.request.details;

import java.util.Objects;

public class RequestUrl {

    private static final String QUERY_STRING_IDENTIFIER = "?";

    private final String url;
    private final QueryParameter queryParameter;

    private RequestUrl(final String url, final QueryParameter queryParameter) {
        this.url = url;
        this.queryParameter = queryParameter;
    }

    public static RequestUrl of(final String parsedRequestUrl) {
        if (parsedRequestUrl.contains(QUERY_STRING_IDENTIFIER)) {
            final int queryParamSeparator = parsedRequestUrl.lastIndexOf(QUERY_STRING_IDENTIFIER);
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
