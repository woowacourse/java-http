package org.apache.coyote.http11.common;

import static nextstep.jwp.exception.ExceptionType.INVALID_RESPONSE_HEADER_EXCEPTION;

import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.InvalidHttpResponseException;

public class HttpHeader {

    private static final String OS_LINE = " " + System.lineSeparator();

    private final Map<HttpHeaderType, List<String>> header;

    private HttpHeader(Map<HttpHeaderType, List<String>> header) {
        this.header = header;
    }

    public static HttpHeader of(HttpHeaderType key, String... value) {
        if (value == null) {
            throw new InvalidHttpResponseException(INVALID_RESPONSE_HEADER_EXCEPTION);
        }
        return new HttpHeader(Map.of(key, List.of(value)));
    }

    public String generateHeaderResponse() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<HttpHeaderType, List<String>> header : header.entrySet()) {
            final String value = joinValues(header.getValue());
            sb.append(String.join(": ", header.getKey().getValue(), value)).append(OS_LINE);
        }
        return sb.toString();
    }

    private String joinValues(List<String> value) {
        if (value.size() > 1) {
            return String.join(";", value);
        }
        return value.get(0);
    }

    public Map<HttpHeaderType, List<String>> getHeader() {
        return header;
    }

}
