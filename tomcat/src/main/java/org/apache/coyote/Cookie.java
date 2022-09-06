package org.apache.coyote;

import java.util.UUID;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.response.ResponseHeaders;

public class Cookie {

    private Cookie() {
    }

    public static void createJsessionidIfNotExists(final RequestHeaders requestHeaders, final ResponseHeaders responseHeaders) {
        if (requestHeaders.hasJsessionid()) {
            return;
        }
        responseHeaders.add("Set-cookie", "JSESSIONID=" + UUID.randomUUID());
    }
}
