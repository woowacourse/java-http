package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.ContentType;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final ResponseHeader header;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, ResponseHeader header, String body) {
        this.httpStatus = httpStatus;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse from(HttpStatus httpStatus, ContentType contentType, String requestBody) {
        return new HttpResponse(httpStatus, new ResponseHeader(contentType, requestBody.getBytes().length),
                requestBody);
    }

    public String getHttpResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getMessage() + " ",
                "Content-Type: " + header.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + header.getContentLength() + " ",
                "", body);
    }
}
