package org.apache.coyote.http11;

public class HttpResponse {
    private final String statusLine;
    private final String contentType;
    private final String body;

    public HttpResponse(String statusLine, String contentType, String body) {
        this.statusLine = statusLine;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse("HTTP/1.1 200 OK", "text/html;charset=utf-8", body);
    }
    
    public static HttpResponse ok(String body, String contentType) {
        return new HttpResponse("HTTP/1.1 200 OK", contentType, body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse("HTTP/1.1 404 Not Found", "text/html;charset=utf-8", "404 Not Found");
    }

    public String toHttpResponse() {
        return String.join("\r\n",
                statusLine + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    public byte[] getBytes() {
        return toHttpResponse().getBytes();
    }
}