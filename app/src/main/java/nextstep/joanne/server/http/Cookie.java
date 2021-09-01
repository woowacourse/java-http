package nextstep.joanne.server.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookie {
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public Cookie() {
        cookies = new LinkedHashMap<>();
    }

    public Cookie(String cookie) {
        cookies = Stream.of(cookie.split(";"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(x -> x[0].trim(), x -> x[1].trim()));
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String makeSessionId() {
        UUID uuid = UUID.randomUUID();
        return JSESSIONID + "=" + uuid.toString();
    }

    public boolean hasSessionId() {
        return cookies.containsKey(JSESSIONID);
    }
}
