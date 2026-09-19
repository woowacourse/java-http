package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookies {

    private final Map<String, String> cookies = new HashMap<>();

    public Cookies(final String cookieHeader) {
        if (cookieHeader.isBlank()) {
            return;
        }

        for (String cookie : cookieHeader.split(";")) {
            final String[] nameAndValue = cookie.split("=", 2);

            if (nameAndValue.length == 2) {
                cookies.put(nameAndValue[0].strip(), nameAndValue[1].strip());
            }
        }
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(cookies.get("JSESSIONID"));
    }
}
