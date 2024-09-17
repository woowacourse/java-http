package org.apache.coyote.http11.message.header;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.http11.message.CookieParser;

public class HttpHeaders {

    private static final String HTTP_HEADER_FIELD_SEPARATOR = "\r\n";
    private static final String HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR = ": ";
    private static final int HTTP_HEADER_FIELD_KEY_INDEX = 0;
    private static final int HTTP_HEADER_FIELD_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpHeaders(final String httpHeaders) {
        validateHttpHeadersIsNullOrBlank(httpHeaders);
        this.values = parseHttpHeaderFields(httpHeaders);
    }

    public HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }

    private void validateHttpHeadersIsNullOrBlank(final String httpHeaders) {
        if (httpHeaders == null || httpHeaders.isBlank()) {
            throw new IllegalArgumentException("HTTP Headers는 null 혹은 빈 값이 입력될 수 없습니다. - " + httpHeaders);
        }
    }

    private Map<String, String> parseHttpHeaderFields(final String httpHeaders) {
        return Arrays.stream(httpHeaders.split(HTTP_HEADER_FIELD_SEPARATOR))
                .map(this::parseHttpHeaderKeyAndValue)
                .collect(Collectors.toMap(filed -> filed[HTTP_HEADER_FIELD_KEY_INDEX],
                        field -> field[HTTP_HEADER_FIELD_VALUE_INDEX]));
    }

    private String[] parseHttpHeaderKeyAndValue(final String httpHeaderField) {
        final String[] fieldKeyAndValue = httpHeaderField.split(HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR, 2);
        if (fieldKeyAndValue.length != 2) {
            throw new IllegalArgumentException("유효하지 않은 HTTP Header Field입니다. - " + httpHeaderField);
        }

        return fieldKeyAndValue;
    }

    public Optional<String> findValueByKey(final HttpHeaderFieldType key) {
        return findValue(key.getValue());
    }

    private Optional<String> findValue(final String key) {
        if (!values.containsKey(key)) {
            return Optional.empty();
        }

        return Optional.of(values.get(key));
    }

    public Optional<String> findValueByKey(final String key) {
        return findValue(key);
    }

    public Map<String, String> parseCookie(final CookieParser parser) {
        return parser.parseCookie(values.get(HttpHeaderFieldType.COOKIE.getValue()));
    }

    public String convertHeaderMessage() {
        StringBuilder sb = new StringBuilder();
        values.keySet()
                .forEach(key ->
                        sb.append(key).append(HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR)
                                .append(values.get(key))
                                .append(HTTP_HEADER_FIELD_SEPARATOR)
                );

        return sb.toString();
    }
}
