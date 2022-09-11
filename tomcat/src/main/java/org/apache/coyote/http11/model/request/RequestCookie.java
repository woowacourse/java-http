package org.apache.coyote.http11.model.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestCookie {

    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private final Map<String, String> cookies;

    private RequestCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static RequestCookie from(final String input) {
        Map<String, String> cookies = Arrays.stream(input.split(COOKIE_DELIMITER))
                .map(splitInput -> splitInput.split(COOKIE_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        cookie -> cookie[KEY_INDEX],
                        cookie -> cookie[VALUE_INDEX]
                ));
        return new RequestCookie(cookies);
    }

    public String getJSessionValue() {
        return cookies.get("JSESSIONID");
    }
}
