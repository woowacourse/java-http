package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.HttpHeader;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final HttpHeader httpHeader;
    private final String responseBody;

    public HttpResponse(final HttpStatus httpStatus, final HttpHeader httpHeader, final String responseBody) {
        this.httpStatus = httpStatus;
        this.httpHeader = httpHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final ResponseEntity responseEntity) {
        return new HttpResponse(responseEntity.getHttpStatus(), responseEntity.getHttpHeader(), responseEntity.getBody());
    }

    public String createResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getMessage() + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                httpHeader.toString(),
                responseBody);
    }
}
