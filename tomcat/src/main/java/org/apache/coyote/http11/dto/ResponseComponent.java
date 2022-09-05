package org.apache.coyote.http11.dto;

import org.apache.coyote.http11.StatusCode;

public class ResponseComponent {

    private StatusCode statusCode;
    private String contentType;
    private String contentLength;
    private String body;

    public ResponseComponent(StatusCode statusCode, String contentType, String contentLength, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.body = body;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public String getBody() {
        return body;
    }
}
