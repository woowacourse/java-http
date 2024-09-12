package org.apache.coyote.http11.request;

public class RequestHeader {

    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final int INDEX_OF_KEY = 0;
    private static final int INDEX_OF_VALUE = 1;

    private final String key;
    private final String value;

    public RequestHeader(String rawHeader) {
        validateFormat(rawHeader);
        this.key = rawHeader.split(KEY_VALUE_DELIMITER)[INDEX_OF_KEY];
        this.value = rawHeader.split(KEY_VALUE_DELIMITER)[INDEX_OF_VALUE];
    }

    private void validateFormat(String raw) {
        if (!raw.contains(KEY_VALUE_DELIMITER)) {
            throw new IllegalArgumentException("유효하지 않은 형태의 헤더입니다.");
        }
    }

    public boolean hasKey(String key) {
        return this.key.equals(key);
    }

    public String getValue() {
        return value;
    }
}
