package org.apache.coyote.http11.message.request;

public class HttpRequestUri {

    private final String value;

    public HttpRequestUri(final String value) {
        validateRequestUriValueIsNullOrBlank(value);
        validateRequestUriStartWithSlash(value);
        this.value = value;
    }

    private void validateRequestUriValueIsNullOrBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Request URI 값으로 null 혹은 빈 값이 입력될 수 없습니다. - " + value);
        }
    }

    private void validateRequestUriStartWithSlash(final String value) {
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("Request URI 값은 /로 시작해야합니다. - " + value);
        }
    }

    public String getValue() {
        return value;
    }
}
