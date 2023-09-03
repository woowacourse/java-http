package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.ContentType;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final String responseBody;
    private final ContentType contentType;
    private final String redirectPage;

    public HttpResponse(HttpStatus httpStatus, String responseBody, ContentType contentType) {
        this(httpStatus, responseBody, contentType, null);
    }

    public HttpResponse(HttpStatus httpStatus, String responseBody, ContentType contentType, String redirectPage) {
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
        this.contentType = contentType;
        this.redirectPage = redirectPage;
    }

    public boolean hasRedirect() {
        return redirectPage != null;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String getResponseWithRedirect() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "Location: " + "http://localhost:8080/" + redirectPage + " ",
                "",
                responseBody);
    }

}
