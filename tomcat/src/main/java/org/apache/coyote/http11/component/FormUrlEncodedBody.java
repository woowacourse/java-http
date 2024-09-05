package org.apache.coyote.http11.component;

import java.util.List;
import java.util.Objects;

public class FormUrlEncodedBody extends Body {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    public FormUrlEncodedBody(final String plaintext) {
        super(plaintext);
    }

    protected void consume(final String plaintext) {
        final var params = List.of(plaintext.split(PARAMETER_DELIMITER));
        params.forEach(this::convertParam);
    }

    private void convertParam(final String param) {
        Objects.requireNonNull(param);
        final var pair = List.of(param.split(KEY_VALUE_DELIMITER));
        if (pair.size() != 2) {
            throw new IllegalArgumentException("올바르지 않은 파라미터 갯수");
        }
        super.add(pair.get(0), pair.get(1));
    }
}
