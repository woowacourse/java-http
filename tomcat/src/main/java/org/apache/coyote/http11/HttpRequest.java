package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;

public class HttpRequest {

    private static final String BLANK = " ";
    private static final char QUERY_STRING_STANDARD = '?';
    private static final int URI_INDEX = 1;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int INDEX_NOT_FOUND = -1;

    private final String url;
    private final HttpMethod httpMethod;
    private final QueryStrings queryStrings;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final String startLine, final HttpRequestBody requestBody) {
        final String uri = extractURI(startLine);

        url = extractURL(uri);
        queryStrings = new QueryStrings(uri);
        httpMethod = HttpMethod.of(extractMethod(startLine));
        httpRequestBody = requestBody;
    }

    private String extractURI(final String startLine) {
        return startLine.split(BLANK)[URI_INDEX];
    }

    private String extractURL(final String uri) {
        int index = uri.lastIndexOf(QUERY_STRING_STANDARD);
        if (index == INDEX_NOT_FOUND) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private String extractMethod(final String startLine) {
        final String[] startLineInfo = startLine.split(BLANK);
        return startLineInfo[HTTP_METHOD_INDEX];
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.httpMethod.equals(httpMethod);
    }

    public String getUrl() {
        return url;
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }
}
