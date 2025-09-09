package org.apache.coyote.http11.httprequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;

public class RequestHeaders {

    private static final String HEADER_SEPARATOR = ": ";

    private final Map<String, String> headers;

    public static RequestHeaders from(final List<String> rawRequestHeaders) {
        final Map<String, String> bodyParameters = new HashMap<>();
        for (String header : rawRequestHeaders) {
            final String[] keyAndValue = header.split(HEADER_SEPARATOR);
            final String key = keyAndValue[0];
            if (keyAndValue.length != 2) {
                throw new HttpStatusException(HttpStatusCode.NOT_FOUND);
            }
            final String value = keyAndValue[1];
            bodyParameters.put(key, value);
        }
        return new RequestHeaders(bodyParameters);
    }

    public String getOrDefault(final String name, final String defaultValue) {
        return headers.getOrDefault(name, defaultValue);
    }

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }
}
