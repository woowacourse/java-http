package nextstep.jwp.model.httpmessage.session;

import nextstep.jwp.util.HttpRequestUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static nextstep.jwp.util.HttpRequestUtils.EQUALS_DELIMITER;

public class HttpCookie {

    public static final String SEMICOLON_DELIMITER = "; ";
    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(String line) {
        this.cookies = splitLine(line);
    }

    public HttpCookie(HttpSession session) {
        this.cookies = new LinkedHashMap<>(Map.of(JSESSIONID, session.getId()));
    }

    private Map<String, String> splitLine(String line) {
        Map<String, String> maps = new LinkedHashMap<>();

        String[] splits = line.split(SEMICOLON_DELIMITER);
        for (String split : splits) {
            Map.Entry<String, String> entry = HttpRequestUtils.splitByEqual(split);
            maps.put(Objects.requireNonNull(entry).getKey(), entry.getValue());
        }

        return maps;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(SEMICOLON_DELIMITER);
        cookies.forEach((key, value) -> stringJoiner.add(key + EQUALS_DELIMITER + value));
        return stringJoiner.toString();
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }
}
