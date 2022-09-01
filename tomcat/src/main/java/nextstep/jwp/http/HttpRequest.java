package nextstep.jwp.http;

import java.util.List;
import java.util.Objects;

public class HttpRequest {

    private final HttpMethod method;
    private final String uriPath;
    private final QueryParams queryParams;

    private HttpRequest(final HttpMethod method,
                        final String uriPath,
                        final QueryParams queryParams) {
        this.method = method;
        this.uriPath = uriPath;
        this.queryParams = queryParams;
    }

    public static HttpRequest from(final String firstLine, final List<String> headers, final String requestBody) {
        final String[] splitFirstLine = getFirstLine(firstLine).split(" ");

        final HttpMethod httpMethod = HttpMethod.from(splitFirstLine[0]);

        final String uriPathAndQueryString = splitFirstLine[1];
        final int queryStringFlagIndex = uriPathAndQueryString.indexOf("?");
        final String uriPath = uriPathAndQueryString.substring(0, queryStringFlagIndex);
        final QueryParams queryParams = QueryParams.from(uriPathAndQueryString.substring(queryStringFlagIndex + 1));

        return new HttpRequest(httpMethod, uriPath, queryParams);
    }

    private static String getFirstLine(final String httpMessage) {
        final String[] rawHttpMessage = httpMessage.split("\r\n", 2);
        return rawHttpMessage[0];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUriPath() {
        return uriPath;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpRequest)) {
            return false;
        }
        final HttpRequest that = (HttpRequest) o;
        return method == that.method
                && Objects.equals(uriPath, that.uriPath)
                && Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uriPath, queryParams);
    }
}
