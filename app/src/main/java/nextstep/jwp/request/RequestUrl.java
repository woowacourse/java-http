package nextstep.jwp.request;

import java.util.Objects;

public class RequestUrl {

    private static final String QUERY_STRING_SEPARATOR = "?";

    private final String requestUrl;
    private final QueryParameter queryParameter;

    public RequestUrl(String requestUrl, QueryParameter queryParameter) {
        this.requestUrl = requestUrl;
        this.queryParameter = queryParameter;
    }

    public static RequestUrl of(String parsedRequestUrl) {
        if (parsedRequestUrl.contains(QUERY_STRING_SEPARATOR)) {
            final int queryParamSeparator = parsedRequestUrl.lastIndexOf("?");
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
        return Objects.equals(requestUrl, that.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestUrl);
    }
}
