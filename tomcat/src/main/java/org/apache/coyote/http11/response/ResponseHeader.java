package org.apache.coyote.http11.response;

import org.apache.coyote.http11.constant.HeaderKey;

public class ResponseHeader {

    private static final String FORMAT_OF_HEADER = "%s: %s ";

    private final HeaderKey key;
    private final String value;

    public ResponseHeader(HeaderKey key, String value) {
        validate(key, value);
        this.key = key;
        this.value = value;
    }

    private void validate(HeaderKey key, String value) {
        validateKey(key);
        validateValue(value);
    }

    private void validateKey(HeaderKey raw) {
        if (raw == null) {
            throw new IllegalArgumentException("헤더의 키는 비어있을 수 없습니다.");
        }
    }

    private void validateValue(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("헤더의 값은 비어있을 수 없습니다.");
        }
    }

    public String buildHttpMessage() {
        return String.format(FORMAT_OF_HEADER, key.getValue(), value);
    }
}
