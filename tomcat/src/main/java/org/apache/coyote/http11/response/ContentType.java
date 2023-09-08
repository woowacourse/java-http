package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    ;

    private final String name;

    public static ContentType from(String contentName) {
        return Arrays.stream(ContentType.values())
                .filter(it -> contentName.endsWith(it.name().toLowerCase()))
                .findFirst()
                .orElse(HTML);
    }
}
