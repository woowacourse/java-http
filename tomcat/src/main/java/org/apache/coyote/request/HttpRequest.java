package org.apache.coyote.request;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.exception.CoyoteHttpException;
import org.apache.coyote.exception.CoyoteIOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HttpRequest {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("^[^?]*\\?[^?]*$");
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_PARAM_START_DELIMITER = "?";
    private static final String HEADER_END_CONDITION = "";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final QueryParams queryParams;
    private final HttpVersion httpVersion;
    private final Headers headers;

    public HttpRequest(final HttpMethod httpMethod, final RequestPath requestPath, final QueryParams queryParams, final HttpVersion httpVersion, final Headers headers) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.queryParams = queryParams;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public static HttpRequest from(final BufferedReader br) {
        try {
            final String[] requestLine = br.readLine().split(REQUEST_LINE_DELIMITER);
            if (requestLine.length != REQUEST_LINE_LENGTH) {
                throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 HttpMethod, URI, HttpVersion가 존재해야 합니다.");
            }

            final Headers headers = parseToHeaders(br);
            final HttpMethod httpMethod = HttpMethod.from(requestLine[HTTP_METHOD_INDEX]);
            final HttpVersion httpVersion = HttpVersion.from(requestLine[HTTP_VERSION_INDEX]);

            final String requestUri = requestLine[HTTP_REQUEST_URI_INDEX];
            RequestPath requestPath = new RequestPath(requestUri);
            QueryParams queryParams = QueryParams.empty();
            if (QUERY_PARAM_PATTERN.matcher(requestUri).matches()) {
                final int queryParamStartIndex = requestUri.indexOf(QUERY_PARAM_START_DELIMITER);
                final String requestPathValue = requestUri.substring(0, queryParamStartIndex);
                final String queryParamsValue = requestUri.substring(queryParamStartIndex);

                requestPath = new RequestPath(requestPathValue);
                queryParams = QueryParams.from(queryParamsValue);
            }

            return new HttpRequest(httpMethod, requestPath, queryParams, httpVersion, headers);

        } catch (IOException e) {
            throw new CoyoteIOException("HTTP 요청 정보를 읽던 도중에 예외가 발생하였습니다.");
        }
    }

    private static Headers parseToHeaders(final BufferedReader br) throws IOException {
        final List<String> headersWithValue = new ArrayList<>();
        String header = br.readLine();
        while (!header.equals(HEADER_END_CONDITION)) {
            headersWithValue.add(header);
            header = br.readLine();
        }

        final Headers headers = new Headers(headersWithValue);
        return headers;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public RequestPath requestUri() {
        return requestPath;
    }

    public QueryParams queryParams() {
        return queryParams;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public Headers headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "HttpRequest{" + System.lineSeparator() +
               "    httpMethod = " + httpMethod + ", " + System.lineSeparator() +
               "    requestPath = " + requestPath + ", " + System.lineSeparator() +
               "    queryParams = " + queryParams + ", " + System.lineSeparator() +
               "    httpVersion = " + httpVersion + ", " + System.lineSeparator() +
               "    headers = " + headers + System.lineSeparator() +
               '}';
    }
}
