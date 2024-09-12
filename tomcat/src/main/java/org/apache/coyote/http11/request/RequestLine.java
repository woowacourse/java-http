package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.NameValuePairs;

public record RequestLine(
        HttpMethod method,
        HttpURL url,
        String version
) {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_IDX = 0;
    private static final int URL_IDX = 1;
    private static final int HTTP_VERSION_IDX = 2;
    private static final int TOKEN_LENGTH = 3;

    public static RequestLine from(String requestLine) {
        String[] tokens = requestLine.split(REQUEST_LINE_DELIMITER);
        validateTokenLength(tokens);
        HttpMethod method = HttpMethod.from(tokens[HTTP_METHOD_IDX]);
        HttpURL url = HttpURL.from(tokens[URL_IDX]);
        String version = tokens[HTTP_VERSION_IDX];
        return new RequestLine(method, url, version);
    }

    private static void validateTokenLength(String[] tokens) {
        if (tokens.length != TOKEN_LENGTH) {
            throw new IllegalArgumentException("HTTP Request Line은 3개의 토큰으로 구성되어야 합니다.");
        }
    }

    public String path() {
        return url().path();
    }

    public NameValuePairs getQueryParameters() {
        return url().queryParameters();
    }
}
