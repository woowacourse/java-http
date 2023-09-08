package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.common.HttpProtocolVersion;

@Getter
@RequiredArgsConstructor
public class HttpResponseStatusLine {

    private final HttpProtocolVersion httpProtocolVersion;
    private final HttpStatus httpStatus;

    public static HttpResponseStatusLine of(HttpProtocolVersion httpProtocolVersion, HttpStatus httpStatus) {
        return new HttpResponseStatusLine(httpProtocolVersion, httpStatus);
    }
}
