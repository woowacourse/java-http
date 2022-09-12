package nextstep.jwp.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> value;

    private HttpCookie(Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie of(String cookie) {
        Map<String, String> value = Arrays.stream(cookie.split("; "))
                .map(e -> e.split("="))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
        return new HttpCookie(value);
    }

    public String getResponse() {
        List<String> responseHeader = new ArrayList<>();
        value.forEach((k, v) -> responseHeader.add(k + "=" + v));
        return String.join("; ", responseHeader);
    }
}
