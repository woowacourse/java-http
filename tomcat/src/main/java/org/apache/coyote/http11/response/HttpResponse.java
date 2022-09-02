package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.HttpStatus;

public class HttpResponse {

    final private String protocol;
    final private HttpStatus status;
    final private String contentType;
    final private int contentLength;
    final private String responseBody;

    public HttpResponse(final String protocol,
                        final HttpStatus status,
                        final String contentType,
                        final String responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.contentType = contentType;
        this.contentLength = responseBody.getBytes().length;
        this.responseBody = responseBody;
    }

    public String parseToString() {
        return String.join("\r\n",
                protocol + " " + status.getCode() + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                responseBody);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "protocol='" + protocol + '\'' +
                ", status='" + status + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentLength=" + contentLength +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
