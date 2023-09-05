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

    public Headers() {
    }

    public Headers(final Map<String, String> headers) {
        mapping.putAll(headers);
    }

    public String getHeaderValue(final String name) {
        return mapping.getOrDefault(name, null);
    }

    public void addHeader(final String headerName, final String value) {
        mapping.put(headerName, value);
    }

    public void setContentType(final String contentType) {
        mapping.put(CONTENT_TYPE.source(), contentType);
    }

    public void setContentLength(final int contentLength) {
        mapping.put(CONTENT_LENGTH.source(), String.valueOf(contentLength));
    }

    public void setLocation(final String location) {
        mapping.put(LOCATION.source(), location);
    }

    public void setCookies(final Cookies cookies) {
        final String cookieValues = cookies.cookieNames()
                .stream()
                .map(cookieName -> cookieName + "=" + cookies.getCookieValue(cookieName))
                .collect(Collectors.joining(";"));

        mapping.put(SET_COOKIE.source(), cookieValues);
    }

    public void addCookie(final String cookieName, final String cookieValue) {
        final String oldSetCookies = mapping.getOrDefault(SET_COOKIE.source(), null);
        if (Objects.isNull(oldSetCookies)) {
            mapping.put(SET_COOKIE.source(), cookieName + "=" + cookieValue);
        }

        mapping.put(SET_COOKIE.source(), oldSetCookies + ";" + cookieName + "=" + cookieValue);
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
