package org.apache.coyote.http11.httprequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String HEADER_SEPARATOR = ": ";

    private final Map<String, String> headers;

    public static RequestHeaders from(final List<String> rawRequestHeaders) {
        final Map<String, String> bodyParameters = rawRequestHeaders.stream()
                .map(param -> param.split(HEADER_SEPARATOR))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1],
                        (oldValue, newValue) -> newValue
                ));

        return new RequestHeaders(bodyParameters);
    }

    public String getOrDefault(final String name, final String defaultValue) {
        return headers.getOrDefault(name, defaultValue);
    }

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }
}
