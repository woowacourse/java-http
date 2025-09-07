package org.apache.coyote.http11;

import java.util.Map;

public record ParseHttpRequest(
        String method,
        String httpRequest,
        Map<String, String> requestBody
) {

    public ParseHttpRequest addRequestBody(Map<String, String> requestBody) {
        return new ParseHttpRequest(method, httpRequest, requestBody);
    }
}
