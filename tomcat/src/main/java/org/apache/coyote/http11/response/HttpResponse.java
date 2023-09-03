package org.apache.coyote.http11.response;

import java.io.IOException;

import org.apache.coyote.http11.request.ContentType;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final String responseBody;
    private final ContentType contentType;

    public HttpResponse(HttpStatus httpStatus, String responseBody, ContentType contentType) {
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public String getResponse() throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

}
