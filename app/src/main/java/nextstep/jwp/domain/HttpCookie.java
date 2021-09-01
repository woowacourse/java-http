package nextstep.jwp.domain;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    public HttpCookie(String cookie) {
        this.values = Stream.of(cookie.split(";"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public String getSessionId() {
        return values.get(JSESSIONID);
    }

    public int size() {
        return values.size();
    }
}
