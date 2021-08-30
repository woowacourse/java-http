package nextstep.jwp.framework.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpCookies {

    private final List<Cookie> cookies;

    public HttpCookies() {
        this(new ArrayList<>());
    }

    public HttpCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(String cookies) {
        return new HttpCookies(HttpCookies.Parser.parse(cookies));
    }

    public HttpCookies addCookie(String name, String value) {
        return addCookie(new Cookie(name, value));
    }

    public HttpCookies addCookie(Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    private static class Parser {
        private static List<Cookie> parse(String cookies) {
            return Arrays.stream(separateCookies(cookies))
                         .map(cookie -> {
                             String[] nameAndValue = separateNameAndValue(cookie);
                             return new Cookie(nameAndValue[0], nameAndValue[1]);
                         })
                         .collect(Collectors.toList());
        }

        private static String[] separateNameAndValue(String cookie) {
            return cookie.split("=");
        }

        private static String[] separateCookies(String cookies) {
            return cookies.split("; ");
        }
    }
}
