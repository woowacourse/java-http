package nextstep.jwp.http.common;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidRequestHeader;
import nextstep.jwp.exception.QueryParameterNotFoundException;

public class HttpCookie {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXPECT_LINE_LENGTH = 2;

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parse(String requestCookie) {
        String[] splitedCookies = requestCookie.split(";");

        Map<String, String> cookies = new HashMap<>();
        for (String cookie : splitedCookies) {
            String[] splitedCookie = splitCookie(cookie);

            String cookieKey = splitedCookie[KEY_INDEX];
            String cookieValue = splitedCookie[VALUE_INDEX];

            cookies.put(cookieKey, cookieValue);
        }

        return new HttpCookie(cookies);
    }

    private static String[] splitCookie(String cookie) {
        String[] splitedCookie = cookie.trim().split("=");

        if (splitedCookie.length != EXPECT_LINE_LENGTH) {
            throw new InvalidRequestHeader();
        }

        return splitedCookie;
    }

    public String getParameter(String parameter) {
        if (cookies.containsKey(parameter.toLowerCase())) {
            return cookies.get(parameter.toLowerCase());
        }

        throw new QueryParameterNotFoundException();
    }
}
