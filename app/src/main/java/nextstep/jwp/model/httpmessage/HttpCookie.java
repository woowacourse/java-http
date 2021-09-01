package nextstep.jwp.model.httpmessage;

import nextstep.jwp.util.HttpRequestUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {

    public static final String SEMICOLON_DELIMITER = ";";
    private final Map<String, String> cookies;

    public HttpCookie(String line) {
        this.cookies = splitLine(line);
    }

    private Map<String, String> splitLine(String line) {
        Map<String, String> maps = new LinkedHashMap<>();
        String[] splits = line.split(SEMICOLON_DELIMITER);
        for (String split : splits) {
            Map.Entry<String, String> entry = HttpRequestUtils.splitByEqual(split.trim());
            maps.put(Objects.requireNonNull(entry).getKey(), entry.getValue());
        }
        return maps;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
