package org.apache.coyote.http11.httpResponse;

public class HttpResponseHeader {

    private final String requestUri;

    public HttpResponseHeader(
            final String requestUri
    ) {
        this.requestUri = requestUri;
    }

    public String getHeader(final int contentLength) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: "+ getContentType() + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "\r\n"
        );
    }

    private String getContentType() {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.equals(".js")) {
            return "text/javascript";
        }
        if (requestUri.equals("svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}
