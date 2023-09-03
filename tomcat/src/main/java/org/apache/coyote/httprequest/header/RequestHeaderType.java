package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidRequestHeaderNameException;

import java.util.Arrays;

public enum RequestHeaderType {
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept");

    private final String headerName;

    RequestHeaderType(final String headerName) {
        this.headerName = headerName;
    }

    public static RequestHeaderType from(final String headerName) {
        return Arrays.stream(values())
                .filter(requestHeaderType -> requestHeaderType.headerName.equals(headerName))
                .findFirst()
                .orElseThrow(InvalidRequestHeaderNameException::new);
    }
}
