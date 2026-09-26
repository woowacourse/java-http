package org.apache.coyote.http11;

import java.util.Map;

public record HttpResponse(
        StatusLine statusLine,
        Map<String, String> headers,
        byte[] body
) {

}
