package org.apache;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    List<String> headers;
    String responseBody;

    private HttpResponse(List<String> headers, String responseBody) {
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(String uri, String responseBody) {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 200 OK ");
        headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
        headers.add("Content-Length: " + calculateContentLength(responseBody) + " ");

        return new HttpResponse(headers, responseBody);
    }

    public static HttpResponse redirect(String uri, String redirectUri) {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 302 Found ");
        headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
        headers.add("Content-Length: " + calculateContentLength(redirectUri) + " ");
        headers.add("Location: " + redirectUri);

        return new HttpResponse(headers, redirectUri);
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
        String responseString = String.join("\r\n", headers) + "\r\n" + "\r\n" + responseBody;
        return responseString.getBytes();
    }
}
