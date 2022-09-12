package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpStatusLine statusLine;
    private final HttpResponseHeader httpResponseHeader;
    private final String responseBody;

    private HttpResponse(final HttpStatus httpStatus, final HttpResponseHeader httpResponseHeader, final String responseBody) {
        this.statusLine = new HttpStatusLine(httpStatus);
        this.httpResponseHeader = httpResponseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final ResponseEntity responseEntity) {
        return new HttpResponse(responseEntity.getHttpStatus(), responseEntity.getHttpHeader(), responseEntity.getBody());
    }

    public String createResponse() {
        return String.join("\r\n",
                statusLine.toString(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                httpResponseHeader.toString(),
                responseBody);
    }
}
