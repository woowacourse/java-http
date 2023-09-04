package org.apache.coyote.httpresponse.header;

import java.util.Arrays;

public enum ResponseHeaderType {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location");

    private final String headerName;

    ResponseHeaderType(final String headerName) {
        this.headerName = headerName;
    }

    public static ResponseHeaderType from(final String headerName) {
        return Arrays.stream(values())
                .filter(responseHeaderType -> responseHeaderType.headerName.equals(headerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("잘못된 응답 헤더 이름 입니다."));
    }

    public String getHeaderName() {
        return headerName;
    }
}
