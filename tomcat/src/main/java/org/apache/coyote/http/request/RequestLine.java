package org.apache.coyote.http.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Map;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpVersion;

public class RequestLine {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String ROOT_PATH = "/";
    private static final String EMPTY = "";

    private static final int REQUEST_LINE_PARTS = 3;
    private static final int NOT_FOUND_INDEX = -1;
    private static final int VERSION_INDEX = 2;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;

    private final HttpMethod httpMethod;
    private final String path;
    private final RequestParams requestParams;
    private final HttpVersion httpVersion;

    public RequestLine(final HttpMethod httpMethod,
                       final String path,
                       final RequestParams requestParams,
                       final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.requestParams = requestParams;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String requestHeaderFirstLine) {
        String[] requestLineValues = splitRequestLine(requestHeaderFirstLine);

        String requestMethod = requestLineValues[METHOD_INDEX];
        String requestUri = requestLineValues[URI_INDEX];
        String requestProtocolVersion = requestLineValues[VERSION_INDEX];

        HttpMethod httpMethod = HttpMethod.from(requestMethod);
        String path = extractPath(requestUri);
        String queryString = extractQueryString(requestUri);
        RequestParams requestParams = RequestParams.from(queryString);

        HttpVersion httpVersion = HttpVersion.from(requestProtocolVersion);

        return new RequestLine(httpMethod, path, requestParams, httpVersion);
    }

    private static String[] splitRequestLine(final String requestLine) {
        validateNullRequestLine(requestLine);
        String[] requestLineValues = requestLine.split(REQUEST_LINE_DELIMITER);
        validateRequestLineFormat(requestLineValues);
        return requestLineValues;
    }

    private static void validateNullRequestLine(final String requestLine) {
        if (requestLine == null) {
            throw new UncheckedServletException("request line은 null이 될 수 없습니다.");
        }
    }

    private static void validateRequestLineFormat(final String[] requestLineValues) {
        if (requestLineValues.length != REQUEST_LINE_PARTS) {
            throw new UncheckedServletException("올바르지 않은 요청 형식입니다.");
        }
    }

    private static String extractPath(final String requestUri) {
        int queryStringDelimiterIndex = findQueryStringDelimiterIndex(requestUri);

        if (hasExtension(queryStringDelimiterIndex)) {
            return requestUri.substring(0, queryStringDelimiterIndex);
        }

        return requestUri;
    }

    private static String extractQueryString(final String requestUri) {
        int queryStringDelimiterIndex = findQueryStringDelimiterIndex(requestUri);

        if (hasExtension(queryStringDelimiterIndex)) {
            return requestUri.substring(queryStringDelimiterIndex + 1);
        }

        return EMPTY;
    }

    private static int findQueryStringDelimiterIndex(final String uri) {
        int firstIndex = uri.indexOf(QUERY_STRING_DELIMITER);
        int lastIndex = uri.lastIndexOf(QUERY_STRING_DELIMITER);

        if (hasExtension(firstIndex) && firstIndex != lastIndex) {
            throw new UncheckedServletException("잘못된 URI 형식: ?가 여러 번 포함되었습니다 → " + uri);
        }

        return firstIndex;
    }

    private static boolean hasExtension(int delimiterIndex) {
        return delimiterIndex != NOT_FOUND_INDEX;
    }

    public String getFilePath() {
        if (path.contains(EXTENSION_DELIMITER)) {
            return path;
        }
        return path + DEFAULT_EXTENSION;
    }

    public boolean isRootPath() {
        return path.equals(ROOT_PATH);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getRequestParams() {
        return requestParams.queryParameters();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
