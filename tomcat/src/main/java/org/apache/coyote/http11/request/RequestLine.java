package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.common.HttpMethod;

public record RequestLine(
        HttpMethod method,
        HttpURL url,
        String version
) {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_IDX = 0;
    private static final int URL_IDX = 1;
    private static final int HTTP_VERSION_IDX = 2;

    public static RequestLine from(String requestLine) {
        String[] tokens = requestLine.split(REQUEST_LINE_DELIMITER);
        HttpMethod method = HttpMethod.from(tokens[HTTP_METHOD_IDX]);
        HttpURL url = HttpURL.from(tokens[URL_IDX]);
        String version = tokens[HTTP_VERSION_IDX];
        return new RequestLine(method, url, version);
    }

    public String path() {
        return url().path();
    }

    public Map<String, String> getQueryParameters() {
        return Collections.unmodifiableMap(url().queryParameters());
    }
}
