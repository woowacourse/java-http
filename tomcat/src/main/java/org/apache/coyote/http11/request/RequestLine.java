package org.apache.coyote.http11.request;

import org.apache.commons.lang3.StringUtils;

public class RequestLine {

    public static final int REQUEST_METHOD_INDEX = 0;
    public static final int REQUEST_URL_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;
    private final HttpMethod httpMethod;
    private final String requestUrl;
    private final String version;

    private RequestLine(HttpMethod httpMethod, String requestUrl, String version) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.version = version;
    }

    public static RequestLine from(String line) {
        String[] splitLine = StringUtils.split(line, " ");
        HttpMethod method = HttpMethod.from(splitLine[REQUEST_METHOD_INDEX]);
        String url = splitLine[REQUEST_URL_INDEX];
        String version = splitLine[HTTP_VERSION_INDEX];

        return new RequestLine(method, url, version);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getMethod() {
        return httpMethod.getMethod();
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
