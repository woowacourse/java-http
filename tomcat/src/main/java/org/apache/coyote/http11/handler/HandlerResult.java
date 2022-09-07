package org.apache.coyote.http11.handler;

import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;

public class HandlerResult {

    private final HttpStatusCode statusCode;
    private final Map<String, String> responseHeader;
    private final String responseBody;

    public HandlerResult(HttpStatusCode statusCode, Map<String, String> responseHeader, String responseBody) {
        this.statusCode = statusCode;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
