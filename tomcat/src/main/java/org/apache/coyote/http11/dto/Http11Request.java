package org.apache.coyote.http11.dto;

import java.util.LinkedHashMap;

public record Http11Request(
        String method,
        String path,
        LinkedHashMap<String, String> headers,
        byte[] body
) {

}
