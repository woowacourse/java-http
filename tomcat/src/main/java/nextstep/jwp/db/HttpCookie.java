package nextstep.jwp.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> value;

    public HttpCookie(String cookie) {
        this.value = Arrays.stream(cookie.split("; "))
                .map(e -> e.split("="))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
    }

    public Map<String, String> getValue() {
        return value;
    }

    public String getResponse() {
        List<String> responseHeader = new ArrayList<>();
        value.forEach((k, v) -> responseHeader.add(k + "=" + v));
        return String.join("; ",
                responseHeader);
    }
}
