package nextstep.jwp.framework.infrastructure.http.cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;

public class HttpCookies {

    private static final String COOKIE_HEADER_DELIMITER = ": ";
    private static final String EACH_COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(List<String> httpRequestHeaders) {
        Optional<String> optionalCookie = httpRequestHeaders.stream()
            .filter(header -> header.startsWith(HttpHeaders.COOKIE.getSignature()))
            .findAny();
        if (optionalCookie.isEmpty()) {
            return new HttpCookies(new HashMap<>());
        }
        String cookies = optionalCookie.get()
            .split(COOKIE_HEADER_DELIMITER, -1)[1];
        return new HttpCookies(parseCookies(cookies));
    }

    private static Map<String, String> parseCookies(String cookies) {
        return Arrays.stream(cookies.split(EACH_COOKIE_DELIMITER, -1))
            .map(cookie -> cookie.split(COOKIE_KEY_VALUE_DELIMITER, -1))
            .collect(Collectors.toMap(split -> split[0], split -> split[1]));
    }

    public String getAttribute(String key) {
        return cookies.get(key);
    }
}
