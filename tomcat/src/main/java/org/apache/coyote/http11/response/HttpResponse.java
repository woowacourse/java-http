package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.ContentType;

public class HttpResponse {

    private final Status status;
    private final ResponseHeader header;
    private final String body;

    public HttpResponse(Status status, ResponseHeader header, String body) {
        this.status = status;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse from(Status status, ContentType contentType, String requestBody) {
        return new HttpResponse(status, new ResponseHeader(contentType, requestBody.getBytes().length), requestBody);
    }

    public String getHttpResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " ",
                "Content-Type: " + header.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + header.getContentLength() + " ",
                "", body);
    }
}
