package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidRequestHeaderNameException;
import org.apache.coyote.httprequest.exception.UnsupportedHeaderTypeException;

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

    public RequestHeader getRequestHeader(final String value) {
        if (this == HOST) {
            return HostHeader.from(value);
        } else if (this == CONNECTION) {
            return ConnectionHeader.from(value);
        } else if (this == ACCEPT) {
            return AcceptHeader.from(value);
        } else {
            throw new UnsupportedHeaderTypeException();
        }
    }
}
