package org.apache.coyote.http11;

public class HttpResponse {

    private final String contentType;
    private final String body;

    private HttpResponse(String contentType, String body) {
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse from(String requestURI, String resource) {
        String contentType = extractContentType(requestURI);
        return new HttpResponse(contentType, resource);
    }

    private static String extractContentType(String requestURI) {
        if (requestURI.contains(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
