package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final String SPLIT_DELIMITER = " ";
    private static final String HOME_PATH = "/";
    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(final HttpMethod httpMethod, final RequestUri requestUri, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final BufferedReader br) throws IOException {
        String startLine = br.readLine();
        String[] startLineElements = startLine.split(SPLIT_DELIMITER);
        HttpMethod httpMethod = HttpMethod.findHttpMethod(startLineElements[0]);
        RequestUri requestUri = new RequestUri(startLineElements[1]);
        HttpVersion httpVersion = new HttpVersion(startLineElements[2].trim());
        return new RequestLine(httpMethod, requestUri, httpVersion);
    }

    public Map<String, Object> getQueryParams() {
        return requestUri.parseQueryParams();
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
