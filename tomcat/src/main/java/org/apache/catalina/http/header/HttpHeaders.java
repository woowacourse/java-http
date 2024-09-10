package org.apache.catalina.http.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.catalina.exception.CatalinaException;

public class HttpHeaders {

    public static final String COOKIE_HEADER = "Cookie: ";
    private static final String SEPARATOR = ": ";

    private final Map<String, String> headers;
    private final HttpCookies cookies;

    public HttpHeaders() {
        this(new HashMap<>(), new HttpCookies());
    }

    public HttpHeaders(Map<String, String> headers, HttpCookies cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static HttpHeaders parse(List<String> httpHeaders) {
        Map<String, String> headers = httpHeaders.stream()
                .filter(header -> !header.startsWith(COOKIE_HEADER))
                .map(HttpHeaders::validateAndSplit)
                .collect(Collectors.toMap(
                        header -> header[0],
                        header -> header[1]
                ));
        HttpCookies httpCookies = httpHeaders.stream()
                .filter(header -> header.startsWith(COOKIE_HEADER))
                .findAny()
                .map(header -> header.substring(COOKIE_HEADER.length()))
                .map(HttpCookies::parse)
                .orElse(new HttpCookies());
        return new HttpHeaders(headers, httpCookies);
    }

    private static String[] validateAndSplit(String header) {
        String[] keyAndValue = header.split(SEPARATOR);
        if (keyAndValue.length == 2) {
            return keyAndValue;
        }
        throw new CatalinaException("Invalid header: " + header);
    }

    public String get(HttpHeader header) {
        String value = headers.get(header.getValue());
        if (value == null) {
            throw new CatalinaException("Header " + header.getValue() + " not found");
        }
        return value;
    }

    public void add(HttpHeader header, String value) {
        headers.put(header.getValue(), value);
    }

    public Optional<String> getSessionFromCookie() {
        return cookies.getSession();
    }

    public void addSessionToCookies(String session) {
        cookies.addSession(session);
    }

    public String stringify(String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        headers.forEach((key, value) -> joiner.add(key + SEPARATOR + value));
        joiner.add(HttpHeader.SET_COOKIE.getValue() + SEPARATOR + cookies.stringify());
        return joiner.toString();
    }
}
