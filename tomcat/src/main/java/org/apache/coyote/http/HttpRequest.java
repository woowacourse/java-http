package org.apache.coyote.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String POST = "POST";
    private static final String GET = "GET";

    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_CSS = "text/css";
    private static final String APPLICATION_JAVASCRIPT = "application/javascript";

    private static final String QUERY_STRING_PREFIX = "?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryParams;
    private final String contentType;
    private final Map<String, String> headers;

    private HttpRequest(final String httpMethod, final String path, final Map<String, String> queryParams,
                        final String contentType,
                        final Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.contentType = contentType;
        this.headers = headers;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers) {
        final String[] splitStartLine = startLine.split(" ");
        final String httpMethod = splitStartLine[0];
        final String uri = splitStartLine[1];
        final String path = getPath(uri);
        final Map<String, String> queryParams = getQueryParams(uri);
        final String contentType = getContentType(path);

        return new HttpRequest(httpMethod, path, queryParams, contentType, headers);
    }

    private static String getPath(final String uri) {
        if (!hasQueryString(uri)) {
            return uri;
        }
        final int prefixIndex = uri.indexOf(QUERY_STRING_PREFIX);
        return uri.substring(0, prefixIndex);
    }

    private static Map<String, String> getQueryParams(final String uri) {
        if (!hasQueryString(uri)) {
            return Collections.emptyMap();
        }
        final int prefixIndex = uri.indexOf(QUERY_STRING_PREFIX);
        final String[] queryStrings = uri.substring(prefixIndex + 1)
                .split(QUERY_STRING_DELIMITER);

        final Map<String, String> queryParams = new HashMap<>();
        for (final String queryString : queryStrings) {
            final String[] splitQueryString = queryString.split(KEY_VALUE_DELIMITER);
            queryParams.put(splitQueryString[0], splitQueryString[1]);
        }
        return queryParams;
    }

    private static boolean hasQueryString(final String uri) {
        return uri.contains(QUERY_STRING_PREFIX);
    }

    private static String getContentType(final String path) {
        if (path.contains(".css")) {
            return TEXT_CSS;
        }
        if (path.contains(".js")) {
            return APPLICATION_JAVASCRIPT;
        }
        return TEXT_HTML;
    }

    public boolean isRegister() {
        return httpMethod.equals(POST) && path.equals("/register");
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(final String key) {
        return queryParams.get(key);
    }

    public String getContentType() {
        return contentType;
    }

    public String getHeader(final String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }
}
