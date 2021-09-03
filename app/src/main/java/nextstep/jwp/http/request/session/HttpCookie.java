package nextstep.jwp.http.request.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = ";";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> cookie;

    public HttpCookie(String rawCookie) {
        if (rawCookie.isEmpty()) {
            this.cookie = new HashMap<>();
            return;
        }
        this.cookie = Arrays.stream(rawCookie.split(COOKIE_DELIMITER))
            .map(cookie -> cookie.split(COOKIE_VALUE_DELIMITER))
            .peek(cookieArr -> {
                cookieArr[KEY_INDEX] = cookieArr[KEY_INDEX].trim();
                cookieArr[VALUE_INDEX] = cookieArr[VALUE_INDEX].trim();
            }).collect(Collectors.toMap(arr -> arr[KEY_INDEX], arr -> arr[VALUE_INDEX]));

    }

    public String asString() {
        return cookie.entrySet().stream()
            .map(entry -> String.format("%s%s%s", entry.getKey(), COOKIE_VALUE_DELIMITER, entry.getValue()))
            .collect(Collectors.joining(String.format("%s ", COOKIE_DELIMITER)));
    }
    // 아래와 같은 쿠키 값을 파싱해서 map으로 저장한다.
    // yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46

}
