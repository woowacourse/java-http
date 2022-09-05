package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final HttpStatusCode httpStatusCode;
    private final Map<String, String> responseHeaders;
    private final String responseBody;

    private HttpResponse(HttpStatusCode httpStatusCode, String responseBody) {
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
        this.responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Length", responseBody.getBytes().length + "");
    }

    public static HttpResponse of(HttpStatusCode httpStatusCode, String responseBody) {
        return new HttpResponse(httpStatusCode, responseBody);
    }

    public HttpResponse setCookie(String cookie) {
        responseHeaders.put("Set-Cookie", cookie);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        responseHeaders.put("Content-Type", contentType);
        return this;
    }

    public HttpResponse setLocation(String location){
        responseHeaders.put("Location", location);
        return this;
    }

    public byte[] getBytes() {
        String httpResponseHeaders = responseHeaders.entrySet().stream()
            .map(it -> it.getKey() + ": " + it.getValue())
            .collect(Collectors.joining(" \r\n"));

        return String.join("\r\n",
            "HTTP/1.1 " + httpStatusCode.toString(),
            httpResponseHeaders,
            "",
            responseBody).getBytes();
    }
}
