package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// 클라이언트로 보낼 응답 메시지를 조립 및 출력
public class HttpResponse {
    private final OutputStream outputStream;
    private String statusLine= "HTTP/1.1 200 OK";
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // 헤더 추가
    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    // 쿠키 추가(Set-Cookie 헤더)
    public void addCookie(final String name, final String value) {
        addHeader("Set-Cookie", name + "=" + value + "; Path=/");
    }

    // 302 리다이렉트 세팅
    public void sendRedirect(String location) {
        this.statusLine = "HTTP/1.1 302 Found";
        addHeader("Location", location);
    }

    // 응답 바디 설정
    public void writeBody(byte[] bodyContent, String contentType) {
        this.body = bodyContent;
        addHeader("Content-Type", contentType);
        addHeader("Content-Length", String.valueOf(bodyContent.length));
    }

    // 모든 조합된 데이터를 소켓으로 최종 출력
    public void flush() throws IOException {
        final StringBuilder sb = new StringBuilder();
        // Status Line
        sb.append(statusLine).append(" \r\n");

        // Headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        sb.append("\r\n"); // 헤더와 바디 사이의 빈 줄

        // 1) 헤더 텍스트 전송
        outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));

        // 2) 바디 데이터 전송 (바디가 존재하는 경우만)
        if (body.length > 0) {
            outputStream.write(body);
        }
        outputStream.flush();

    }
}
