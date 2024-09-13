package org.apache.coyote.http11.message.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.message.CookieParser;

public class HttpHeader {

    private static final String HTTP_HEADER_FIELDS_SEPARATOR = "\r\n";
    private static final String HEADER_FIELD_KEY_AND_VALUE_SEPARATOR = ": ";
    private static final int HEADER_FIELD_KEY_AND_VALUE_SIZE = 2;
    private static final int HEADER_FIELD_KEY_INDEX = 0;
    private static final int HEADER_FIELD_VALUE_INDEX = 1;

    private final Map<String, String> fields;

    public HttpHeader(final Map<String, String> fields) {
        this.fields = fields;
    }

    public HttpHeader(final String httpRequestHeaderValue) {
        validateHttpRequestHeaderValueIsNullOrBlank(httpRequestHeaderValue);
        this.fields = parseHttpHeaderFields(httpRequestHeaderValue);
    }

    private void validateHttpRequestHeaderValueIsNullOrBlank(final String httpRequestHeaderValue) {
        if (httpRequestHeaderValue == null || httpRequestHeaderValue.isBlank()) {
            throw new IllegalArgumentException(
                    "HTTP Request Header 값은 Null 혹은 빈 값이 올 수 없습니다. - " + httpRequestHeaderValue);
        }
    }

    private Map<String, String> parseHttpHeaderFields(final String value) {
        final Map<String, String> fields = new HashMap<>();
        final String[] fieldValues = value.split(HTTP_HEADER_FIELDS_SEPARATOR);
        Arrays.stream(fieldValues)
                .forEach(fieldValue -> {
                    final String[] fieldKeyAndValue = splitFieldKeyAndValue(fieldValue);
                    fields.put(fieldKeyAndValue[HEADER_FIELD_KEY_INDEX], fieldKeyAndValue[HEADER_FIELD_VALUE_INDEX]);
                });

        return fields;
    }

    private String[] splitFieldKeyAndValue(final String field) {
        final String[] keyAndValue = field.trim().split(HEADER_FIELD_KEY_AND_VALUE_SEPARATOR);
        if (field.isBlank()) {
            return new String[]{"", ""};
        }
        if (keyAndValue.length != HEADER_FIELD_KEY_AND_VALUE_SIZE) {
            throw new IllegalArgumentException("유효하지 않은 Header 필드 값 입니다. - " + field);
        }

        return keyAndValue;
    }

    public Optional<String> findValueByKey(final HttpHeaderFieldType key) {
        return findValue(key.getValue());
    }

    private Optional<String> findValue(final String key) {
        if (!fields.containsKey(key)) {
            return Optional.empty();
        }

        return Optional.of(fields.get(key));
    }

    public Optional<String> findValueByKey(final String key) {
        return findValue(key);
    }

    public Map<String, String> parseCookie(final CookieParser parser) {
        return parser.parseCookie(fields.get(HttpHeaderFieldType.COOKIE.getValue()));
    }

    public String convertHeaderMessage() {
        StringBuilder sb = new StringBuilder();
        fields.keySet()
                .forEach(key ->
                        sb.append(key).append(HEADER_FIELD_KEY_AND_VALUE_SEPARATOR)
                                .append(fields.get(key))
                                .append(HTTP_HEADER_FIELDS_SEPARATOR)
                );

        return sb.toString();
    }
}
