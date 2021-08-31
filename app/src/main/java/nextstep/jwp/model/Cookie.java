package nextstep.jwp.model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    public Cookie(String cookie) {
        this.values = Stream.of(cookie.split(";"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    public String getSessionId() {
        return values.get(JSESSIONID);
    }
}
