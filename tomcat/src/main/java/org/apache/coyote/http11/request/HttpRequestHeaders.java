package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestHeaders {

    private static final String contentTypeKey = "Accept";

    private static final String contentLengthKey = "Content-Length";

    private static final String DELIMITER = ",";

    private final Map<String, String> headers;

    public HttpRequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContentType() {
        return this.headers.get(contentTypeKey).split(DELIMITER)[0];
    }

    public int getContentLength() {
        return Integer.parseInt(this.headers.get(contentLengthKey));
    }
}
