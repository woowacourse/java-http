package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final HttpResponseHeader httpResponseHeader;
    private final String responseBody;

    public HttpResponse(final HttpStatus httpStatus, final HttpResponseHeader httpResponseHeader, final String responseBody) {
        this.httpStatus = httpStatus;
        this.httpResponseHeader = httpResponseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final ResponseEntity responseEntity) {
        return new HttpResponse(responseEntity.getHttpStatus(), responseEntity.getHttpHeader(), responseEntity.getBody());
    }

    public String createResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getMessage() + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                httpResponseHeader.toString(),
                responseBody);
    }
}
