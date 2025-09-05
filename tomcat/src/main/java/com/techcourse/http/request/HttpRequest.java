package com.techcourse.http.request;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.common.ContentType;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

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

        String requestUri = requestLineValues[1];

        String path = extractPath(requestUri);
        RequestParams requestParams = RequestParams.from(extractQueryString(requestUri));
        ContentType contentType = extractContentType(path);

        return new HttpRequest(path, requestParams, contentType);
    }

    private static String[] splitRequestLine(final String requestLine) {
        validateNullRequestLine(requestLine);
        String[] requestLineValues = requestLine.split(" ");
        validateRequestLineFormat(requestLineValues);
        return requestLineValues;
    }

    private static void validateNullRequestLine(final String requestLine) {
        if (requestLine == null) {
            throw new UncheckedServletException("request line은 null이 될 수 없습니다.");
        }
    }

    private static void validateRequestLineFormat(final String[] requestLineValues) {
        if (requestLineValues.length != 3) {
            throw new UncheckedServletException("올바르지 않은 요청 형식입니다.");
        }
    }

    private static String extractPath(final String requestUri) {
        int queryStringStartIndex = findSingleDelimiterIndex(requestUri, "?");

        if (queryStringStartIndex != -1) {
            return requestUri.substring(0, queryStringStartIndex);
        }

        return requestUri;
    }

    private static String extractQueryString(final String requestUri) {
        int queryStringStartIndex = findSingleDelimiterIndex(requestUri, "?");

        if (queryStringStartIndex != -1) {
            return requestUri.substring(queryStringStartIndex + 1);
        }

        return "";
    }

    private static ContentType extractContentType(final String path) {
        Objects.requireNonNull(path);
        int delimiterIndex = findSingleDelimiterIndex(path, ".");

        if (delimiterIndex != -1) {
            String extension = path.substring(delimiterIndex + 1);
            return ContentType.from(extension);
        }
        return ContentType.TEXT_HTML;
    }

    private static int findSingleDelimiterIndex(String uri, String delimiter) {
        int firstIndex = uri.indexOf(delimiter);
        int lastIndex = uri.lastIndexOf(delimiter);

        if (firstIndex != -1 && firstIndex != lastIndex) {
            throw new UncheckedServletException("잘못된 URI 형식: " + delimiter + " 가 여러 번 포함되었습니다 → " + uri);
        }

        return firstIndex;
    }

    public String getFilePath() {
        if (path.contains(".")) {
            return path;
        }
        return path + ".html";
    }

    public boolean isRootPath() {
        return path.equals("/");
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getRequestParams() {
        return requestParams.queryParameters();
    }

    public ContentType getContentType() {
        return contentType;
    }
}
