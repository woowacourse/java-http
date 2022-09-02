package org.apache.coyote.http11;

public class HttpResponse {

    private final String statusCode;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(final String statusCode, final ContentType contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType.toString() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
