package org.apache.coyote.http11;

public class HttpResponse {

    final private String protocol;
    final private String status;
    final private String contentType;
    final private String contentLength;
    final private String responseBody;

    public HttpResponse(final String protocol, final String status, final String contentType,
                        final String contentLength, final String responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.responseBody = responseBody;
    }
}
