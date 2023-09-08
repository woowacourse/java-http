package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.exception.CoyoteHttpException;

import java.util.regex.Pattern;

public class RequestLine {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("^[^?]*\\?[^?]*$");
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_PARAM_START_DELIMITER = "?";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final HttpVersion httpVersion;
    private final RequestPath requestPath;
    private final QueryParams queryParams;

    private RequestLine(final HttpMethod httpMethod, final HttpVersion httpVersion, final RequestPath requestPath, final QueryParams queryParams) {
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
        this.requestPath = requestPath;
        this.queryParams = queryParams;
    }

    public static RequestLine from(final String requestLine) {
        final String[] requestLineValues = requestLine.split(REQUEST_LINE_DELIMITER);
        if (requestLineValues.length != REQUEST_LINE_LENGTH) {
            throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 HttpMethod, URI, HttpVersion가 존재해야 합니다.");
        }

        final HttpMethod httpMethod = HttpMethod.from(requestLineValues[HTTP_METHOD_INDEX]);
        final HttpVersion httpVersion = HttpVersion.from(requestLineValues[HTTP_VERSION_INDEX]);

        final String requestUri = requestLineValues[HTTP_REQUEST_URI_INDEX];
        QueryParams queryParams = QueryParams.empty();
        RequestPath requestPath = new RequestPath(requestUri);
        if (QUERY_PARAM_PATTERN.matcher(requestUri).matches()) {
            final int queryParamStartIndex = requestUri.indexOf(QUERY_PARAM_START_DELIMITER);
            final String requestPathValue = requestUri.substring(0, queryParamStartIndex);
            final String queryParamNamesAndValues = requestUri.substring(queryParamStartIndex);

            requestPath = new RequestPath(requestPathValue);
            queryParams = QueryParams.from(queryParamNamesAndValues);
        }

        return new RequestLine(httpMethod, httpVersion, requestPath, queryParams);
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
    public String toString() {
        return "RequestLine{" +
               "httpMethod=" + httpMethod +
               ", httpVersion=" + httpVersion +
               ", requestPath=" + requestPath +
               ", queryParams=" + queryParams +
               '}';
    }
}
