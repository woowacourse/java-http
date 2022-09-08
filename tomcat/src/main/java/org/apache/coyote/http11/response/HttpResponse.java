package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";

    private final String responseLine;
    private final ResponseBody body;
    private final ResponseHeaders headers;

    public HttpResponse(String responseLine, ResponseHeaders header, ResponseBody body) {
        this.responseLine = responseLine;
        this.headers = header;
        this.body = body;
    }

    public static HttpResponse create(HttpStatus status, ResponseHeaders header, ResponseBody body) {
        return new HttpResponse(HTTP_VERSION + status.code, header, body);
    }

    public byte[] getBytes() {
        String response = String.join("\r\n", responseLine, headers.getStringHeaders(), body.getBody());
        return response.getBytes();
    }

    public String getResponseLine() {
        return responseLine;
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }

    public ResponseBody getBody() {
        return body;
    }
}
