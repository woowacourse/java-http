package org.apache.coyote.http11.model;

import java.util.Map;

public class HttpResponse {

    private final HttpStatusCode httpStatusCode;
    private final HttpHeaders responseHeaders;
    private final String responseBody;

    private HttpResponse(HttpStatusCode httpStatusCode, String responseBody) {
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
        this.responseHeaders = new HttpHeaders(
            Map.of("Content-Length", String.valueOf(responseBody.getBytes().length)));
    }

    public static HttpResponse of(HttpStatusCode httpStatusCode, String responseBody) {
        return new HttpResponse(httpStatusCode, responseBody);
    }

    public HttpResponse setCookie(String cookie) {
        responseHeaders.addAttribute("Set-Cookie", cookie);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        responseHeaders.addAttribute("Content-Type", contentType);
        return this;
    }

    public HttpResponse setLocation(String location) {
        responseHeaders.addAttribute("Location", location);
        return this;
    }

    public byte[] getBytes() {

        return String.join("\r\n",
            "HTTP/1.1 " + httpStatusCode.toString(),
            responseHeaders.toMessage(),
            "",
            responseBody).getBytes();
    }
}
