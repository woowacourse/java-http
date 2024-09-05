package org.apache;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    List<String> headers;
    byte[] responseBody;

    private HttpResponse(List<String> headers, byte[] responseBody) {
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(String uri, String responseBody) {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 200 OK ");
        headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
        headers.add("Content-Length: " + calculateContentLength(responseBody) + " ");

        return new HttpResponse(headers, responseBody.getBytes(StandardCharsets.UTF_8));
    }

    public static HttpResponse okWithContentType(byte[] responseBody, String contentType) {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 200 OK ");
        headers.add("Content-Type: " + contentType + " ");
        headers.add("Content-Length: " + responseBody.length + " ");

        return new HttpResponse(headers, responseBody);
    }

    public static HttpResponse redirect(String uri, String redirectUri) {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 302 Found ");
        headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
        headers.add("Content-Length: " + calculateContentLength(redirectUri) + " ");
        headers.add("Location: " + redirectUri);

        return new HttpResponse(headers, redirectUri.getBytes(StandardCharsets.UTF_8));
    }

    public static HttpResponse notFound() {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 404 Not Found");
        headers.add("Content-Type: text/html; charset=UTF-8");
        String body = "<h1>404 - Not Found</h1>";
        headers.add("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);

        return new HttpResponse(headers, body.getBytes(StandardCharsets.UTF_8));
    }

    private static int calculateContentLength(String content) {
        return content.replaceAll("\r\n", "\n").getBytes(StandardCharsets.UTF_8).length;
    }

    private static String getContentType(String uri) {
        if (uri.startsWith("/css")) {
            return "text/css";
        }
        return "text/html";
    }

    public void setCookie(String key, String value) {
        for (String header : headers) {
            if (header.startsWith("Set-Cookie")) {
                String newCookie = header + " " + key + "=" + value + ";";
                headers.add(newCookie);
                headers.remove(header);
                return;
            }
        }
        headers.add("Set-Cookie: " + key + "=" + value + ";");
    }

    public byte[] getBytes() {
        String headerString = String.join("\r\n", headers) + "\r\n" + "\r\n";
        byte[] headerBytes = headerString.getBytes(StandardCharsets.UTF_8);

        byte[] fullResponse = new byte[headerBytes.length + responseBody.length];
        System.arraycopy(headerBytes, 0, fullResponse, 0, headerBytes.length);
        System.arraycopy(responseBody, 0, fullResponse, headerBytes.length, responseBody.length);

        return fullResponse;
    }
}
