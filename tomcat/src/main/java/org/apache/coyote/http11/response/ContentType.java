package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    ;

    private final String name;
}
