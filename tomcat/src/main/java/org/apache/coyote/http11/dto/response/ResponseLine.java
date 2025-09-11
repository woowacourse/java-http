package org.apache.coyote.http11.dto.response;

import static org.apache.coyote.http11.HttpConstants.SPACE;

public record ResponseLine(
        String protocolVersion,
        Status status
) {

    @Override
    public String toString() {
        return protocolVersion + SPACE + status.line();
    }
}
