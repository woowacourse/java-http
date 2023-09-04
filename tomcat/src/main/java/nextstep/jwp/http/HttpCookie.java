package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String line) {
        if ("".equals(line)) {
            return new HttpCookie(Collections.emptyMap());
        }

        Map<String, String> cookies = Arrays.stream(line.split("; "))
                .map(element -> element.split("="))
                .collect(Collectors.toMap(element -> element[0], element -> element[1]));

        return new HttpCookie(cookies);
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String getJSessionId() {
        if (containsKey("JSESSIONID")) {
            return cookies.get("JSESSIONID");
        }

        return null;
    }

}
