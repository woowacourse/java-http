package org.apache.coyote.http11.response;

import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.ContentType;

public class Http11Response {

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;

    public Http11Response(final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
