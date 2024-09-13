package org.apache.coyote.http11.httpresponse;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaderName;

public class HttpResponseHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String RESPONSE_LINE_DELIMITER = " \r\n";

    private final Map<HttpHeaderName, String> headers;

    public HttpResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(HttpHeaderName headerName, String value) {
        headers.put(headerName, value);
    }

    public String createHeadersResponse() {
        return headers.keySet().stream()
                .map(key -> key.getName() + HEADER_DELIMITER + headers.get(key))
                .collect(Collectors.joining(RESPONSE_LINE_DELIMITER));
    }

    public Map<HttpHeaderName, String> getHeaders() {
        return headers;
    }
}
