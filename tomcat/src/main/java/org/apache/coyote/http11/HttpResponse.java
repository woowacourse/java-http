package org.apache.coyote.http11;

public class HttpResponse {
    private StatusCode statusCode;
    private String contentType;
    private int contentLength;
    private String body;

    public HttpResponse() {
        this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        this.contentType = StaticResourceHandler.TEXT_HTML_CHARSET_UTF_8;
        this.contentLength = 0;
        this.body = "";
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                body).getBytes();
    }

    public void setContentType(String resourcePath) {
        this.contentType = StaticResourceHandler.getContentType(resourcePath);
    }

    public void setBodyAndContentLength(String resourcePath) {
        this.body = StaticResourceHandler.getResource(resourcePath);
        this.contentLength = body.getBytes().length;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
