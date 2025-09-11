package org.apache.coyote.dto;

import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;

public record HttpRequest(
        RequestLine requestLine,
        HttpCookie cookie
) {
    // 편의 메서드들
    public String method() { return requestLine.method(); }
    public String path() { return requestLine.path(); }
    public String version() { return requestLine.version(); }
    public Map<String, String> queryParams() { return requestLine.queryParams(); }
}