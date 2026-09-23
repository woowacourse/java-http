package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final String status;
    private final List<String> headers;
    private final String body;

    private HttpResponse(String status, List<String> headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create(String status, String contentType, String body) {
        if (contentType.startsWith("text/")) {
            contentType += ";charset=utf-8";
        }

        List<String> headers = new ArrayList<>();
        headers.add("Content-Type: " + contentType);
        headers.add("Content-Length: " + body.getBytes().length + " ");

        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse redirect(String location) {
        List<String> headers = new ArrayList<>();
        headers.add("Location: " + location);
        headers.add("Content-Length: 0");

        return new HttpResponse("302 Found", headers, "");
    }

    public static HttpResponse redirectWithCookie(String location, String cookie) {
        List<String> headers = new ArrayList<>();
        headers.add("Location: " + location);
        headers.add("Set-Cookie: " + cookie);
        headers.add("Content-Length: 0");

        return new HttpResponse("302 Found", headers, "");
    }

    public String toHttpMessage() {
        List<String> response = new ArrayList<>();
        response.add("HTTP/1.1 " + status);
        response.addAll(headers);
        response.add("");
        response.add(body);

        return String.join("\r\n", response);
    }

    public String getBody() {
        return body;
    }
}
