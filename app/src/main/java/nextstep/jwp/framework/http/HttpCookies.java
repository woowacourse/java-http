package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.framework.util.StringUtils;

public class HttpCookies {

    private final Map<String, Cookie> cookies;

    public HttpCookies() {
        this(new HashMap<>());
    }

    public HttpCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(String cookies) {
        return new HttpCookies(HttpCookies.Parser.parse(cookies));
    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }

    public String getValueBy(String name) {
        return getCookie(name).getValue();
    }

    public HttpCookies putCookie(String name, String value) {
        return putCookie(new Cookie(name, value));
    }

    public HttpCookies putCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
        return this;
    }

    public boolean contains(String name) {
        return cookies.containsKey(name);
    }

    private static class Parser {
        private static final int NAME_INDEX = 0;
        private static final int VALUE_INDEX = 1;

        private static Map<String, Cookie> parse(String cookies) {
            final Map<String, Cookie> cookieMap = new HashMap<>();
            for (String cookie : separateCookies(cookies)) {
                final String[] nameAndValue = separateNameAndValue(cookie);
                cookieMap.put(nameAndValue[NAME_INDEX], new Cookie(nameAndValue[NAME_INDEX], nameAndValue[VALUE_INDEX]));
            }
            return cookieMap;
        }

        private static String[] separateNameAndValue(String cookie) {
            return cookie.split("=");
        }

        private static String[] separateCookies(String cookies) {
            if (!StringUtils.hasLength(cookies)) {
                return new String[]{};
            }
            return cookies.split("; ");
        }
    }
}
