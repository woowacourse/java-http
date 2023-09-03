package org.apache.coyote.http11.cookie;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {
    private final Map<String, String> cookies = new ConcurrentHashMap<>();

    public HttpCookie() {
    }

    // JSESSIONID 값 설정
    public void addJSessionId(String sessionId) {
        cookies.put("JSESSIONID", sessionId);
    }

    // JSESSIONID 값 가져오기
    public String getJSessionId() {
        return cookies.get("JSESSIONID");
    }

    // 서버 응답 헤더에 Set-Cookie 추가
    public String addCookieInResponseHeader() {
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\r\n");
        header.append("Content-Length: 5571\r\n");
        header.append("Content-Type: text/html;charset=utf-8\r\n");
        header.append("Set-Cookie: JSESSIONID=").append(getJSessionId()).append("\r\n");
        return header.toString();
    }
}
