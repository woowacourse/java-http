package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final String mimeType;
    private final String responseBody;

    public HttpResponse(final HttpStatus httpStatus, final String mimeType, final String responseBody) {
        this.httpStatus = httpStatus;
        this.mimeType = mimeType;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final ResponseEntity responseEntity) {
        return new HttpResponse(responseEntity.getHttpStatus(), responseEntity.getMimeType(), responseEntity.getBody());
    }

    public String createResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getMessage() + " ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
