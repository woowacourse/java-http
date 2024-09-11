package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String cookies) {
        this.cookies = new HashMap<>();
        initialize(cookies);
    }

    public static String setCookie(String response, String sessionId) {
        return String.join("\r\n",
                response + " ",
                "Set-Cookie: " + "JSESSIONID" + "=" + sessionId + " "
        );
    }

    private void initialize(String cookieString) {
        if (cookieString.isEmpty()) {
            return;
        }
        String[] cookie = cookieString.split(";");
        for (String keyValue : cookie) {
            String[] split = keyValue.split("=");
            cookies.put(split[0].trim(), split[1]);
        }
    }

    public String getCookie(String cookieName) {
        String cookie = cookies.get(cookieName);
        if (cookie == null) {
            return "";
        }
        return cookie;
    }
}
