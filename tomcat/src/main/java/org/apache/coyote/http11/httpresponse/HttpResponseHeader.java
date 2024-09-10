package org.apache.coyote.http11.httpresponse;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaderName;

public class HttpResponseHeader {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<HttpHeaderName, String> headers;

    public HttpResponseHeader(Map<HttpHeaderName, String> headers) {
        this.headers = headers;
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        int size = headers.keySet().size();
        int i = 1;
        for (HttpHeaderName key : headers.keySet()) {
            if (i < size) {
                sb.append(key.getName()).append(HEADER_DELIMITER).append(headers.get(key)).append(" \r\n");
                size++;
            } else {
                sb.append(key.getName()).append(HEADER_DELIMITER).append(headers.get(key));
            }
        }

        return sb.toString();
    }

    public Map<HttpHeaderName, String> getHeaders() {
        return headers;
    }
}
