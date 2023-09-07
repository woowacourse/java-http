package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.BadRequestException;

public class HttpCookie {

    private static final String ATTRIBUTE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_SIZE = 2;

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String line) {
        if (line == null) {
            throw new BadRequestException("Cookie Line is Null");
        }

        Map<String, String> cookies = Arrays.stream(line.split(ATTRIBUTE_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(param -> param.length == KEY_VALUE_SIZE)
                .collect(Collectors.toMap(param -> param[KEY_INDEX], param -> param[VALUE_INDEX]));

        return new HttpCookie(cookies);
    }

    public boolean containsKey(String key) {
        validateKey(key);

        return cookies.containsKey(key);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new BadRequestException("Cookie key is Null");
        }
    }

    public String get(String key) {
        validateKey(key);

        return cookies.get(key);
    }

}
