package org.apache.coyote.http.response;

import common.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import common.HttpConstants;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpStatusLine {

    private final String version;
    private final HttpStatus status;

    public static HttpStatusLine from(
            final String version,
            final HttpStatus status
    ) {
        return new HttpStatusLine(version, status);
    }

    @Override
    public String toString() {
        return HttpConstants.HTTP_PROTOCOL_PREFIX + version
                + " " + status.getCode()
                + " " + status.getReasonPhrase()
                + HttpConstants.CRLF;
    }
}
