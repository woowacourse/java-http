package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpResponseBody {

    private final String body;

    public static HttpResponseBody from(String body) {
        return new HttpResponseBody(body);
    }
}
