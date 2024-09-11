package org.apache.coyote.http11.cookie;

import java.util.ArrayList;
import java.util.List;

public class Cookies {
    private List<Cookie> cookies = new ArrayList<>();

    public boolean hasName(String name) {
        return cookies.stream()
                .anyMatch(cookie -> cookie.getName().equals(name));
    }

    public void setCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public boolean hasCookie() {
        return !cookies.isEmpty();
    }

    public String toMessage() {
        StringBuilder message = new StringBuilder();
        for (Cookie cookie : cookies) {
            message.append("%s=%s; ".formatted(cookie.getName(), cookie.getValue()));
        }

        return message.toString();
    }
}
