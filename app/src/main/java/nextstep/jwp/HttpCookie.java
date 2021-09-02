package nextstep.jwp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String httpCookies) {
        cookies = new HashMap<>(
                Arrays.stream(httpCookies.split("; "))
                      .map(httpCookie -> httpCookie.split("="))
                      .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]))
        );
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public boolean hasSessionId() {
        return cookies.containsKey("JSESSIONID");
    }
}
