package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String requestURL;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, String requestURL, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURL = requestURL;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] line = Formatter.toRequestLineFormat(requestLine);

        HttpMethod httpMethod = HttpMethod.from(line[METHOD_INDEX]);
        String requestURL = line[PATH_INDEX];
        HttpVersion httpVersion = HttpVersion.from(line[VERSION_INDEX]);

        return new RequestLine(httpMethod, requestURL, httpVersion);
    }

    public boolean isGetMethod() {
        return httpMethod.isGet();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
