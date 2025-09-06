package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final ContentType contentType;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    public HttpResponse(HttpStatusCode statusCode, ContentType contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType.get();
    }

    public String getBody() {
        return body;
    }
}
