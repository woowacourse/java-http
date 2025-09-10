package org.apache.coyote.http11.request.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class RequestHeadersParser {

    private static final String HEADER_SEPARATOR = ": ";

    private static final RequestHeadersParser INSTANCE = new RequestHeadersParser();

    public static RequestHeadersParser getInstance() {
        return INSTANCE;
    }

    public Map<String, String> parseRequestHeaders(final List<String> rawRequestHeaders) {
        final Map<String, String> headers = new HashMap<>();
        for (String header : rawRequestHeaders) {
            final String[] keyAndValue = header.split(HEADER_SEPARATOR);
            final String key = keyAndValue[0];
            if (keyAndValue.length != 2) {
                throw new HttpStatusException(HttpStatusCode.NOT_FOUND);
            }
            final String value = keyAndValue[1];
            headers.put(key, value);
        }
        return headers;
    }

    private RequestHeadersParser() {
    }
}
