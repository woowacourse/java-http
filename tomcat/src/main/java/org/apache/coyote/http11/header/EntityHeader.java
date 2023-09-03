package org.apache.coyote.http11.header;

import java.util.Arrays;

public enum EntityHeader implements Header {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    ;

    final String value;

    EntityHeader(final String value) {
        this.value = value;
    }

    public static EntityHeader from(final String headerName) {
        return Arrays.stream(values())
                .filter(entityHeader -> entityHeader.value.equalsIgnoreCase(headerName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("엔티티 헤더에 없는 헤더입니다."));
    }

    @Override
    public String getValue() {
        return value;
    }
}
