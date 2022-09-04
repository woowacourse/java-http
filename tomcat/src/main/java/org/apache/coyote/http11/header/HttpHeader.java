package org.apache.coyote.http11.header;

import java.util.List;

public class HttpHeader {

    private final String httpHeaderType;
    private final List<String> values;

    public HttpHeader(final String httpHeaderType, final List<String> values) {
        this.httpHeaderType = httpHeaderType;
        this.values = values;
    }

    public static HttpHeader of(final String httpHeaderType,
                                final String... values) {
        return new HttpHeader(httpHeaderType, List.of(values));
    }

    public String getHttpHeaderType() {
        return httpHeaderType;
    }

    public List<String> getValues() {
        return values;
    }
}
