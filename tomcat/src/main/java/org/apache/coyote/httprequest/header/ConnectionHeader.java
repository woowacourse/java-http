package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidConnectionHeaderException;

import java.util.Arrays;

public enum ConnectionHeader implements RequestHeader {
    KEEP_ALIVE("keep-alive"),
    CLOSE("close");

    private final String option;

    ConnectionHeader(final String option) {
        this.option = option;
    }

    public static ConnectionHeader from(final String option) {
        return Arrays.stream(values())
                .filter(connectionHeader -> connectionHeader.option.equals(option))
                .findFirst()
                .orElseThrow(InvalidConnectionHeaderException::new);
    }

    @Override
    public String getValue() {
        return option;
    }
}
