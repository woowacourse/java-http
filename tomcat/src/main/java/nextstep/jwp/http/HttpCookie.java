package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String ATTRIBUTE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String J_SESSION_ID = "JSESSIONID";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String line) {
        if ("".equals(line)) {
            return new HttpCookie(Collections.emptyMap());
        }

        Map<String, String> cookies = Arrays.stream(line.split(ATTRIBUTE_DELIMITER))
                .map(element -> element.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(element -> element[KEY_INDEX], element -> element[VALUE_INDEX]));

        return new HttpCookie(cookies);
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String getJSessionId() {
        if (containsKey(J_SESSION_ID)) {
            return cookies.get(J_SESSION_ID);
        }

        return null;
    }

}
