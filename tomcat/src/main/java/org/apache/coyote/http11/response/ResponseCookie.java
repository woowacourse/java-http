package org.apache.coyote.http11.response;

public class ResponseCookie {

    private static final String FORMAT_OF_COOKIE = "%s=%s";

    private final String key;
    private final String value;

    public ResponseCookie(String key, String value) {
        validate(key, value);
        this.key = key;
        this.value = value;
    }

    private void validate(String key, String value) {
        validateNull(key);
        validateNull(value);
        validateFormat(key);
        validateFormat(value);
    }

    private void validateNull(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("쿠키의 키 또는 값은 비어있을 수 없습니다.");
        }
    }

    private void validateFormat(String raw) {
        if (raw.contains("=")) {
            throw new IllegalArgumentException("쿠키의 키 또는 값에 = 문자가 입력될 수 없습니다.");
        }
    }

    public String buildHttpMessage() {
        return String.format(FORMAT_OF_COOKIE, key, value);
    }
}
