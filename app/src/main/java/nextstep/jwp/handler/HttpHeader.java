package nextstep.jwp.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHeader {
    private final Map<String, String> headerMap;

    public HttpHeader(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public int getContentLength() {
        String contentLength = headerMap.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength);
    }

    public void addHeader(String key, String value) {
        this.headerMap.put(key, value);
    }

    public HttpCookie getCookie() {
        HttpCookie httpCookie = new HttpCookie();
        if (headerMap.containsKey("Cookie")) {
            String[] cookies = headerMap.get("Cookie").split("; ");
            for (String cookie : cookies) {
                String[] cookieKeyValue = cookie.split("=");
                httpCookie.setCookie(new Cookie(cookieKeyValue[0], cookieKeyValue[1]));
            }
        }
        return httpCookie;
    }

    public String makeHttpMessage(HttpCookie cookies) {
        List<String> headers = new ArrayList<>();
        for (Map.Entry<String, String> entry: headerMap.entrySet()) {
            String join = String.join(": ", entry.getKey(), entry.getValue() + " ");
            headers.add(join);
        }

        Map<String, Cookie> cookieMap = cookies.getCookieMap();
        for (Cookie cookie : cookieMap.values()) {
            headers.add("Set-Cookie: " + cookie.makeSetCookieHttpMessage() + " ");
        }

        return String.join("\r\n", headers);
    }
}
