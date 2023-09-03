package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidConnectionHeaderException;

import java.util.Arrays;

public enum ConnectionHeader implements RequestHeader {
    KEEP_ALIVE("keep-alive"),
    CLOSE("close");

    private final String printedName;

    ConnectionHeader(final String printedName) {
        this.printedName = printedName;
    }

    public static ConnectionHeader from(final String printedName) {
        return Arrays.stream(values())
                .filter(connectionHeader -> connectionHeader.printedName.equals(printedName))
                .findFirst()
                .orElseThrow(InvalidConnectionHeaderException::new);
    }

    @Override
    public String getValue() {
        return printedName;
    }
}
