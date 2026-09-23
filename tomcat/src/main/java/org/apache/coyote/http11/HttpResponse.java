package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

import java.util.Map;

public record HttpResponse(
        String path,
        HttpStatus httpStatus,
        Map<String, String> headers
) {
}