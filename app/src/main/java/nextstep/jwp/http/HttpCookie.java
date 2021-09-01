package nextstep.jwp.http;

import com.google.common.base.Splitter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String REQUEST_COOKIE_HEADER = "Cookie";
    private static final String COOKIE_DELIMITER = " ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String SUFFIX = ";";
    private static final String RESPONSE_COOKIE_HEADER = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public static HttpCookie fromHeader(HttpHeader httpHeader) {
        String cookies = httpHeader.getValueByKey(REQUEST_COOKIE_HEADER);
        if (cookies != null) {
            Map<String, String> split = Splitter.on(COOKIE_DELIMITER)
                .withKeyValueSeparator(KEY_VALUE_DELIMITER)
                .split(cookies.replace(SUFFIX, ""));

            return new HttpCookie(split);
        }
        return new HttpCookie(new HashMap<>());
    }

    public static HttpCookie ofSessionCookie() {
        return new HttpCookie(new HashMap<>(Map.of(
            JSESSIONID, UUID.randomUUID().toString()
        )));
    }

    public Map<String, String> toHeaderFormat() {
        List<String> keyValues = cookies.entrySet().stream()
            .map(it -> it.getKey() + KEY_VALUE_DELIMITER + it.getValue())
            .collect(Collectors.toList());

        return new HashMap<>(Map.of(RESPONSE_COOKIE_HEADER,
            String.join(SUFFIX + COOKIE_DELIMITER, keyValues)));
    }

    public Map<String, String> getAllCookies() {
        return Collections.unmodifiableMap(cookies);
    }

    public boolean containsSessionId() {
        return cookies.containsKey(JSESSIONID);
    }
}
