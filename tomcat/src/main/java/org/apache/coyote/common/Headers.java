package org.apache.coyote.common;

import org.apache.coyote.session.Cookies;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.common.HeaderType.CONTENT_TYPE;
import static org.apache.coyote.common.HeaderType.LOCATION;
import static org.apache.coyote.common.HeaderType.SET_COOKIE;

public class Headers {

    private final Map<String, String> mapping = new HashMap<>();

    private Headers() {
    }

    public static Headers empty() {
        return new Headers();
    }

    public String getHeaderValue(final String headerName) {
        return mapping.getOrDefault(headerName, null);
    }

    public void addHeader(final String headerName, final String headerValue) {
        mapping.put(headerName, headerValue);
    }

    public Headers setContentType(final String contentType) {
        mapping.put(CONTENT_TYPE.value(), contentType);
        return this;
    }

    public Headers setContentLength(final int contentLength) {
        mapping.put(CONTENT_LENGTH.value(), String.valueOf(contentLength));
        return this;
    }

    public Headers setLocation(final String location) {
        mapping.put(LOCATION.value(), location);
        return this;
    }

    public Headers setCookies(final Cookies cookies) {
        final String cookieValues = cookies.cookieNames()
                .stream()
                .map(cookieName -> cookieName + "=" + cookies.getCookieValue(cookieName))
                .collect(Collectors.joining(";"));

        mapping.put(SET_COOKIE.value(), cookieValues);

        return this;
    }

    public Headers addCookie(final String cookieName, final String cookieValue) {
        final String oldSetCookies = mapping.getOrDefault(SET_COOKIE.value(), null);
        if (Objects.isNull(oldSetCookies)) {
            mapping.put(SET_COOKIE.value(), cookieName + "=" + cookieValue);
            return this;
        }

        mapping.put(SET_COOKIE.value(), oldSetCookies + ";" + cookieName + "=" + cookieValue);
        return this;
    }

    public List<String> headerNames() {
        return mapping.keySet()
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        final String mappingResult = mapping.keySet()
                .stream()
                .map(headerName -> "        " + headerName + " : " + mapping.get(headerName))
                .collect(Collectors.joining("," + System.lineSeparator()));

        return "Headers{" + System.lineSeparator() +
               mappingResult +
               '}';
    }
}
