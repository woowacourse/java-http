package org.apache.coyote.http11.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.session.HttpCookie;

@Getter
@RequiredArgsConstructor
@Builder
public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String requestURI;
    private final String location;
    private final String responseBody;
    private final HttpCookie httpCookie = HttpCookie.empty();

    public void setCookie(String key, String value) {
        httpCookie.put(key, value);
    }
}
