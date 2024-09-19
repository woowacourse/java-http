package org.apache.coyote.http11.message.header;

import org.apache.commons.lang3.StringUtils;

public class HttpHeaderField {

    private static final String HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR = ": ";
    private static final int HTTP_HEADER_FIELD_KEY_AND_VALUE_SET_SIZE = 2;
    private static final int HTTP_HEADER_FIELD_KEY_INDEX = 0;
    private static final int HTTP_HEADER_FIELD_VALUE_INDEX = 1;

    private final String key;
    private final String value;

    public HttpHeaderField(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public HttpHeaderField(final String httpHeaderField) {
        validateHttpHeaderFieldIdNullOrBlank(httpHeaderField);
        final String[] keyAndValue = parseHttpHeaderKeyAndValue(httpHeaderField);
        this.key = keyAndValue[HTTP_HEADER_FIELD_KEY_INDEX];
        this.value = keyAndValue[HTTP_HEADER_FIELD_VALUE_INDEX];
    }

    private void validateHttpHeaderFieldIdNullOrBlank(final String httpHeaderField) {
        if (StringUtils.isBlank(httpHeaderField)) {
            throw new IllegalArgumentException("HTTP Header Field는 null 혹은 빈 값이 입력될 수 없습니다. - " + httpHeaderField);
        }
    }

    private String[] parseHttpHeaderKeyAndValue(final String httpHeaderField) {
        final String[] fieldKeyAndValue = httpHeaderField.split(HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR, 2);
        if (fieldKeyAndValue.length != HTTP_HEADER_FIELD_KEY_AND_VALUE_SET_SIZE) {
            throw new IllegalArgumentException("유효하지 않은 HTTP Header Field입니다. - " + httpHeaderField);
        }

        return fieldKeyAndValue;
    }

    public boolean matchKey(final String key) {
        return this.key.equals(key);
    }

    public String convertHeaderFieldMessage() {
        return key + HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR + value;
    }

    public String getValue() {
        return value;
    }
}
