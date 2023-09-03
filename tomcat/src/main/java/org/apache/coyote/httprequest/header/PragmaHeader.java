package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidPragmaHeaderException;

import java.util.Arrays;

public enum PragmaHeader implements RequestHeader {
    NO_CACHE("no-cache");

    private final String option;

    PragmaHeader(final String option) {
        this.option = option;
    }

    public static PragmaHeader from(final String option) {
        return Arrays.stream(values())
                .filter(pragmaHeader -> pragmaHeader.option.equals(option))
                .findFirst()
                .orElseThrow(InvalidPragmaHeaderException::new);
    }

    @Override
    public String getValue() {
        return option;
    }
}
