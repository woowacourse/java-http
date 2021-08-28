package nextstep.jwp.request;

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
}
