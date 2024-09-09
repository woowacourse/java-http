package org.apache.coyote.http11;

public class Http11Response {

    private final String statusCode;
    private final String contentType;
    private final String body;

    public Http11Response(String statusCode, String contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public String serializeResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
