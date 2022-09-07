package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.util.StringParser;

public class HttpRequest {

    private static final String START_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String QUERY_STRING_FIELD_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";
    private static final int METHOD = 0;
    private static final int PATH = 1;

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryParams;
    private final RequestHeader header;
    private final HttpRequestBody requestBody;

    private HttpRequest(final HttpMethod httpMethod, final String path, final Map<String, String> queryParams,
                        final RequestHeader header, final HttpRequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.header = header;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final String[] startLine = reader.readLine()
                .split(START_LINE_DELIMITER);
        final String uri = startLine[PATH];

        final RequestHeader header = RequestHeader.from(reader);
        final HttpRequestBody requestBody = HttpRequestBody.of(reader, header.getContentLength());

        return new HttpRequest(HttpMethod.from(startLine[METHOD]), toPath(uri), toQueryParams(uri), header,
                requestBody);
    }

    private static String toPath(final String uri) {
        if (!hasQueryString(uri)) {
            return uri;
        }
        final int prefixIndex = uri.indexOf(QUERY_STRING_PREFIX);
        return uri.substring(METHOD, prefixIndex);
    }

    private static Map<String, String> toQueryParams(final String uri) {
        if (!hasQueryString(uri)) {
            return Collections.emptyMap();
        }
        final int prefixIndex = uri.indexOf(QUERY_STRING_PREFIX);
        final String queryString = uri.substring(prefixIndex + PATH);

        return StringParser.split(queryString, QUERY_STRING_FIELD_DELIMITER, QUERY_STRING_KEY_VALUE_DELIMITER);
    }

    private static boolean hasQueryString(final String uri) {
        return uri.contains(QUERY_STRING_PREFIX);
    }

    public RequestHeader getHeader() {
        return header;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpRequestBody getRequestBody() {
        return requestBody;
    }
}
