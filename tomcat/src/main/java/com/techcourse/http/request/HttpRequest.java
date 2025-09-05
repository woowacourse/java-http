package com.techcourse.http.request;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.common.ContentType;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String ROOT_PATH = "/";
    private static final String EMPTY = "";
    private static final String DEFAULT_EXTENSION = ".html";

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final int REQUEST_LINE_PARTS = 3;
    private static final int NOT_FOUND_INDEX = -1;

    private final String path;
    private final RequestParams requestParams;
    private final ContentType contentType;

    public HttpRequest(final String path,
                       final RequestParams requestParams,
                       final ContentType contentType
    ) {
        this.path = path;
        this.requestParams = requestParams;
        this.contentType = contentType;
    }

    public static HttpRequest from(final String requestHeaderFirstLine) {
        String[] requestLineValues = splitRequestLine(requestHeaderFirstLine);

        String requestMethod = requestLineValues[METHOD_INDEX];
        String requestUri = requestLineValues[URI_INDEX];
        String requestVersion = requestLineValues[VERSION_INDEX];

        String path = extractPath(requestUri);
        RequestParams requestParams = RequestParams.from(extractQueryString(requestUri));
        ContentType contentType = extractContentType(path);

        return new HttpRequest(path, requestParams, contentType);
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
        int queryStringStartIndex = findQueryStringDelimiterIndex(requestUri);

        if (queryStringStartIndex != NOT_FOUND_INDEX) {
            return requestUri.substring(0, queryStringStartIndex);
        }

        return requestUri;
    }

    private static String extractQueryString(final String requestUri) {
        int queryStringStartIndex = findQueryStringDelimiterIndex(requestUri);

        if (queryStringStartIndex != NOT_FOUND_INDEX) {
            return requestUri.substring(queryStringStartIndex + 1);
        }

        return EMPTY;
    }

    private static int findQueryStringDelimiterIndex(String uri) {
        int firstIndex = uri.indexOf(QUERY_STRING_DELIMITER);
        int lastIndex = uri.lastIndexOf(QUERY_STRING_DELIMITER);

        if (firstIndex != NOT_FOUND_INDEX && firstIndex != lastIndex) {
            throw new UncheckedServletException("잘못된 URI 형식: ?가 여러 번 포함되었습니다 → " + uri);
        }

        return firstIndex;
    }

    private static ContentType extractContentType(final String path) {
        Objects.requireNonNull(path);
        int extensionDelimiterIndex = path.lastIndexOf(EXTENSION_DELIMITER);

        if (extensionDelimiterIndex != NOT_FOUND_INDEX) {
            String extension = path.substring(extensionDelimiterIndex + 1);
            return ContentType.from(extension);
        }
        return ContentType.TEXT_HTML;
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

    public Map<String, String> getRequestParams() {
        return requestParams.queryParameters();
    }

    public ContentType getContentType() {
        return contentType;
    }
}
