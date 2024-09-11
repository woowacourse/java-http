package org.apache.catalina.http.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.catalina.exception.CatalinaException;

public class HttpHeaders {

    private static final String SEPARATOR = ": ";
    public static final String COOKIE_HEADER = HttpHeader.COOKIE.getValue() + SEPARATOR;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

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
        Map<String, String> headers = parseHeaders(httpHeaders);
        HttpCookies httpCookies = parseCookies(httpHeaders);
        return new HttpHeaders(headers, httpCookies);
    }

    private static Map<String, String> parseHeaders(List<String> httpHeaders) {
        return httpHeaders.stream()
                .filter(header -> !header.startsWith(COOKIE_HEADER))
                .map(HttpHeaders::validateAndSplit)
                .collect(Collectors.toMap(
                        header -> header[KEY_INDEX],
                        header -> header[VALUE_INDEX]
                ));
    }

    private static HttpCookies parseCookies(List<String> httpHeaders) {
        return httpHeaders.stream()
                .filter(header -> header.startsWith(COOKIE_HEADER))
                .findAny()
                .map(header -> header.substring(COOKIE_HEADER.length()))
                .map(HttpCookies::parse)
                .orElse(new HttpCookies());
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

    public void addToCookies(String key, String value) {
        cookies.add(key, value);
    }

    public Optional<String> getFromCookies(String key) {
        return cookies.get(key);
    }

    public String stringify(String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        headers.forEach((key, value) -> joiner.add(key + SEPARATOR + value));
        joiner.add(HttpHeader.SET_COOKIE.getValue() + SEPARATOR + cookies.stringify());
        return joiner.toString();
    }
}
