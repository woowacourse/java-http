package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HttpProtocolVersion {

    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2"),
    HTTP3("HTTP/3"),
    ;

    private final String name;
}
