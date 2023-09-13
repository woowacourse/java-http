package org.apache.coyote.http11.request;

import java.util.Arrays;

public class RequestBody {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String value;

    public RequestBody(final String value) {
        this.value = value;
    }

    public String getParameter(final String key) {
        final String[] tokens = value.split(PARAMETER_DELIMITER);
        return Arrays.stream(tokens)
                .filter(it -> it.split(KEY_VALUE_DELIMITER)[0]
                        .equals(key))
                .map(it -> it.split(KEY_VALUE_DELIMITER)[1])
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(key + "에 대한 정보가 존재하지 않습니다."));
    }
}
