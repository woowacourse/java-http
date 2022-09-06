package org.apache.coyote.http11.request;

import org.apache.commons.lang3.StringUtils;

public class HttpRequestLine {

    private static final int REQUEST_URL_INDEX = 1;
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String requestUrl;
    private final String version;

    public HttpRequestLine(final HttpMethod httpMethod, final String requestUrl, final String version) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.version = version;
    }

    public static HttpRequestLine parse(final String line) {
        final String[] generalStrings = StringUtils.split(line, " ");
        final HttpMethod requestMethod = HttpMethod.from(generalStrings[REQUEST_METHOD_INDEX]);
        final String requestUrl = generalStrings[REQUEST_URL_INDEX];
        final String version = generalStrings[REQUEST_VERSION_INDEX];
        return new HttpRequestLine(requestMethod, requestUrl, version);
    }

    public String getMethod() {
        return httpMethod.getMethod();
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
