package org.apache.coyote.httprequest.header;

import java.util.Arrays;
import java.util.function.Function;

public enum RequestHeaderType {
    HOST("Host", HostHeader::from),
    CONNECTION("Connection", ConnectionHeader::from),
    ACCEPT("Accept", AcceptHeader::from),
    CONTENT_LENGTH("Content-Length", ContentLengthHeader::new),
    UNSUPPORTED_HEADER("Unsupported-Header", UnsupportedHeader::new);

    private final String headerName;
    private final Function<String, RequestHeader> headerFactory;

    RequestHeaderType(final String headerName, final Function<String, RequestHeader> headerFactory) {
        this.headerName = headerName;
        this.headerFactory = headerFactory;
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

    public String saveValue(final String value) {
        return headerFactory.apply(value).getValue();
    }
}
