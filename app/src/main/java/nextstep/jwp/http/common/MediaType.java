package nextstep.jwp.http.common;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum MediaType {
    TEXT_HTML_CHARSET_UTF8("text/html;charset=utf-8");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public static MediaType from(String mediaTypeValue) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.value.equals(mediaTypeValue))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("존재하지 않는 미디어 타입입니다.(%s)", mediaTypeValue))
                );
    }

    public String getValue() {
        return value;
    }
}
