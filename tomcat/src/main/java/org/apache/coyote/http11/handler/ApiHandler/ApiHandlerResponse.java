package org.apache.coyote.http11.handler.ApiHandler;

import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class ApiHandlerResponse {

    private final HttpStatus httpStatus;
    private final Headers headers;
    private final Object body;
    private final ContentType contentType;

    public ApiHandlerResponse(HttpStatus httpStatus, Headers headers, Object body, ContentType contentType) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
        this.contentType = contentType;
    }

    public static ApiHandlerResponse of(HttpStatus httpStatus,
                                        Map<String, String> headers,
                                        String body,
                                        ContentType contentType) {
        return new ApiHandlerResponse(httpStatus, new Headers(headers), body, contentType);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body.toString();
    }

    public ContentType getContentType() {
        return contentType;
    }
}
