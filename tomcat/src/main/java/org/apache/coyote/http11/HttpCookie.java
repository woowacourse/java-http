package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String cookies) {
        this.cookies = new HashMap<>();
        initialize(cookies);
    }

    public static String setCookie(String response) {
        return String.join("\r\n",
                response + " ",
                "Set-Cookie: " + "JSESSIONID" + "=" + UUID.randomUUID() + " "
        );
    }

    private void initialize(String cookieString) {
        String[] cookie = cookieString.split(";");

        for (String keyValue : cookie) {
            String[] split = keyValue.split("=");
            cookies.put(split[0].trim(), split[1]);
        }
    }
}
