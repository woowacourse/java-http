package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String contentTypeKey = "Accept";

    private static final String DELIMITER = ",";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContentType() {
        return this.headers.get(contentTypeKey).split(DELIMITER)[0];
    }
}
