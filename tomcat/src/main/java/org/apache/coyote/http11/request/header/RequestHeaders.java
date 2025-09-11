package org.apache.coyote.http11.request.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public static RequestHeaders from(final List<String> rawRequestHeaders) {
        final RequestHeadersParser parser = RequestHeadersParser.getInstance();
        final Map<String, String> headers = new HashMap<>(parser.parseRequestHeaders(rawRequestHeaders));
        return new RequestHeaders(headers);
    }

    public String getOrDefault(final String name, final String defaultValue) {
        return headers.getOrDefault(name, defaultValue);
    }

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }
}
