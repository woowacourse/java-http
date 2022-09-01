package org.apache.coyote.http11;

public class HttpResponse {

    private final String statusLine;
    private final String headers;
    private final String body;

    public HttpResponse(String statusLine, String headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                statusLine,
                headers,
                "",
                body).getBytes();
    }
}
