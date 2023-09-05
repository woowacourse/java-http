package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.UnsupportedHeaderTypeException;

import java.util.Arrays;

public enum RequestHeaderType {
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    UNSUPPORTED_HEADER("지원하지 않는 타입"),
    CONTENT_LENGTH("Content-Length");

    private final String headerName;

    RequestHeaderType(final String headerName) {
        this.headerName = headerName;
    }

    public static RequestHeaderType from(final String headerName) {
        return Arrays.stream(values())
                .filter(requestHeaderType -> requestHeaderType.headerName.equals(headerName))
                .findFirst()
                .orElse(UNSUPPORTED_HEADER);
    }

    public boolean isUnsupportedHeader() {
        return this == UNSUPPORTED_HEADER;
    }

    public RequestHeader saveRequestHeader(final String value) {
        if (this == HOST) {
            return HostHeader.from(value);
        } else if (this == CONNECTION) {
            return ConnectionHeader.from(value);
        } else if (this == ACCEPT) {
            return AcceptHeader.from(value);
        } else if (this == CONTENT_LENGTH) {
            return new ContentLengthHeader(value);
        } else {
            throw new UnsupportedHeaderTypeException();
        }
    }
}
