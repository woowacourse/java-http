package org.apache.coyote.http11.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String requestURI;
    private final String location;
    private final String responseBody;
}
