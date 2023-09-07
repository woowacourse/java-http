package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpResponseStatusLine {

    private HttpProtocolVersion httpProtocolVersion;
    private HttpResponseHeaders httpResponseHeaders;
}
