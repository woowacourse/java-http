package org.apache.coyote.http11.header;

import java.util.Arrays;

public enum RequestHeader implements Header {
    ACCEPT("Accept"),
    ;

    final String value;

    RequestHeader(final String value) {
        this.value = value;
    }

    public static RequestHeader from(final String headerName) {
        return Arrays.stream(values())
                .filter(requestHeader -> requestHeader.value.equalsIgnoreCase(headerName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("리퀘스트 헤더에 없는 헤더입니다."));
    }

    @Override
    public String getValue() {
        return value;
    }
}
