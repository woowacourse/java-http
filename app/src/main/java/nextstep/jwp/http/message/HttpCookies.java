package nextstep.jwp.http.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpCookies {
    public static final HttpCookies EMPTY_COOKIES = new HttpCookies(Collections.emptyList());

    private List<HttpCookie> cookies;

    public HttpCookies() {
        cookies = new ArrayList<>();
    }

    public HttpCookies(HttpCookie ... httpCookies) {
        List<HttpCookie> values = new ArrayList<>();
        for (HttpCookie httpCookie : httpCookies) {
            values.add(httpCookie);
        }
        this.cookies = values;
    }

    public HttpCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies parseFrom(String cookie) {
        String[] splitted = cookie.split(";");
        List<HttpCookie> cookies = new ArrayList<>();
        for (String s : splitted) {
            cookies.add(HttpCookie.parseFrom(s.trim()));
        }
        return new HttpCookies(cookies);
    }

    public Optional<HttpCookie> findByName(String name) {
        return cookies.stream()
            .filter(httpCookie -> httpCookie.isSameName(name))
            .findFirst();
    }

    public List<HttpCookie> getCookies() {
        return cookies;
    }

    public void addCookie(HttpCookie httpCookie) {
        this.cookies.add(httpCookie);
    }

    public void removeCookieByName(String name) {
        this.cookies
            .removeIf(httpCookie -> httpCookie.isSameName(name));
    }

    public String asString() {
        List<String> cookieAsStrings = cookies
            .stream()
            .map(HttpCookie::asString)
            .collect(Collectors.toList());
        return String.join("; ", cookieAsStrings);
    }
}
