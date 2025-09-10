package org.apache.coyote.util.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String statusLine;
    private final String contentType;
    private final byte[] body;
    private final Map<String, String> headers = new HashMap<>();

    private HttpResponse(String statusLine, String contentType, byte[] body) {
        this.statusLine = statusLine;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse of(String statusLine, String resourcePath) {
        String contentType = HttpContentTypeResolver.resolve(resourcePath);
        byte[] body = readStaticFile(resourcePath);
        return new HttpResponse(statusLine, contentType, body);
    }

    public static HttpResponse of(String statusLine, String contentType, byte[] body) {
        return new HttpResponse(statusLine, contentType, body);
    }

    private static byte[] readStaticFile(String path) {
        try (InputStream is = HttpResponse.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return "404 Not Found".getBytes();
            }
            return is.readAllBytes();
        } catch (IOException e) {
            return "500 Internal Server Error".getBytes();
        }
    }

    public String createHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(statusLine).append(" \r\n");
        builder.append("Content-Type: ").append(contentType).append(" \r\n");
        builder.append("Content-Length: ").append(body.length).append(" \r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }

    public static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse(
                "HTTP/1.1 302 Found ",
                "text/plain;charset=utf-8 ",
                "".getBytes()
        );
        response.addHeader("Location", location);
        return response;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(String name, String value) {
        // SameSite=Lax: CSRF 방어. 대부분의 경우 CSRF를 막아주면서, GET 요청 링크를 통한 세션은 유지시켜줌.
        // HttpOnly: 클라이언트 측 스크립트가 쿠키에 접근하는 것을 방지 (XSS 보호).
        // Path=/: 쿠키를 전체 사이트에서 사용하도록 설정.
        // Secure: HTTPS를 사용하는 경우에만 쿠키를 전송하도록 함. (현재는 HTTP 환경이므로 주석 처리)
        String cookieValue = String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax", name, value);
        addHeader("Set-Cookie", cookieValue);
    }

    public byte[] getBody() {
        return body;
    }
}
