package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;

import java.util.Objects;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final HttpVersion httpVersion;
    private final RequestPath requestPath;
    private final QueryParams queryParams;

    private RequestLine(final HttpMethod httpMethod,
                        final HttpVersion httpVersion,
                        final RequestPath requestPath,
                        final QueryParams queryParams
    ) {
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
        this.requestPath = requestPath;
        this.queryParams = queryParams;
    }

    public static RequestLine of(final String httpMethodValue,
                                 final String httpVersionValue,
                                 final String requestPathValue,
                                 final String queryParamNamesAndValues
    ) {
        return new RequestLine(
                HttpMethod.from(httpMethodValue),
                HttpVersion.from(httpVersionValue),
                RequestPath.from(requestPathValue),
                QueryParams.from(queryParamNamesAndValues)
        );
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public RequestPath requestPath() {
        return requestPath;
    }

    public QueryParams queryParams() {
        return queryParams;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestLine that = (RequestLine) o;
        return httpMethod == that.httpMethod && httpVersion == that.httpVersion && Objects.equals(requestPath, that.requestPath) && Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, httpVersion, requestPath, queryParams);
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "httpMethod=" + httpMethod +
               ", httpVersion=" + httpVersion +
               ", requestPath=" + requestPath +
               ", queryParams=" + queryParams +
               '}';
    }
}
