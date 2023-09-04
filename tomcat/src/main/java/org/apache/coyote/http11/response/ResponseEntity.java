package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String requestURI;
    private final String responseBody;

    public static ResponseEntity of(
            HttpStatus httpStatus,
            String requestURI,
            String responseBody
    ) {
        return new ResponseEntity(httpStatus, requestURI, responseBody);
    }
}
