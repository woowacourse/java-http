package org.apache.coyote.http11.request;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.enums.HttpMethod;

public class HttpRequest {

    private static final String BLANK = " ";
    private static final String QUERY_STRING_STANDARD = "?";
    private static final int URI_INDEX = 1;
    private static final int HTTP_METHOD_INDEX = 0;

    private final String path;
    private final HttpMethod httpMethod;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    public HttpRequest(final String startLine, final HttpRequestHeader requestHeader,
                       final HttpRequestBody requestBody) {
        path = extractURI(startLine);
        httpMethod = HttpMethod.of(extractMethod(startLine));
        headers = requestHeader;
        body = requestBody;
    }

    private String extractURI(final String startLine) {
        return startLine.split(BLANK)[URI_INDEX];
    }

    private String extractMethod(final String startLine) {
        final String[] startLineInfo = startLine.split(BLANK);
        return startLineInfo[HTTP_METHOD_INDEX];
    }

    public boolean isGetMethod() {
        return this.httpMethod.equals(HttpMethod.GET);
    }

    public boolean isPostMethod() {
        return this.httpMethod.equals(HttpMethod.POST);
    }

    public String getUrl() {
        return StringUtils.substringBeforeLast(path, QUERY_STRING_STANDARD);
    }

    public QueryStrings getQueryStrings() {
        return new QueryStrings(path);
    }

    public HttpRequestHeader getHeaders() {
        return headers;
    }

    public HttpRequestBody getBody() {
        return body;
    }
}
