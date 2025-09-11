package org.apache.coyote.http11.dto.request;

import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        RequestHeader requestHeader,
        RequestBody requestBody
) {

    public String getMethod() {
        return requestLine.method();
    }

    public Map<String, String> getBodyQueryParam() {
        return requestBody.getQueryParam();
    }

    public boolean containsCookie(final String name) {
        return requestHeader.containsCookie(name);
    }

    public String getCookie(final String name) {
        return requestHeader.getCookie(name);
    }
}
