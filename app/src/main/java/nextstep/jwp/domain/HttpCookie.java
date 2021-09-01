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
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    public String getSessionId() {
        return values.get(JSESSIONID);
    }
}
