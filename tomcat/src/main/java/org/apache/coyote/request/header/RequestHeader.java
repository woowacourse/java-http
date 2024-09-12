package org.apache.coyote.request.header;

import org.apache.catalina.session.Session;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.util.ContentType;
import org.apache.coyote.util.HttpHeaders;
import org.apache.coyote.util.MapMaker;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestHeader {

    private static final String HEADER_SPLITTER = ":";
    private static final String HEADER_SEPARATOR = ",";
    private static final int FIRST_HEADER_INDEX = 0;
    private static final int EMPTY_BODY_LENGTH = 0;

    private final Map<HttpHeaders, String> headerValue;

    public RequestHeader(List<String> inputHeader) {
        this.headerValue = initHeader(inputHeader);
    }

    private Map<HttpHeaders, String> initHeader(List<String> inputHeader) {
        return inputHeader.stream()
                .map(header -> header.split(HEADER_SPLITTER))
                .collect(MapMaker.toMap(HttpHeaders::findHeader));
    }

    public int getBodyLength() {
        if (headerValue.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            return Integer.parseInt(headerValue.get(HttpHeaders.CONTENT_LENGTH));
        }
        return EMPTY_BODY_LENGTH;
    }

    public ContentType findAcceptType() {
        String firstAcceptType = headerValue.get(HttpHeaders.ACCEPT).split(HEADER_SEPARATOR)[FIRST_HEADER_INDEX];

        return ContentType.findContentTypeByHeader(firstAcceptType);
    }

    public boolean hasCookie() {
        String cookie = headerValue.get(HttpHeaders.COOKIE);
        return cookie != null;
    }

    public Session findSession() {
        String cookie = headerValue.get(HttpHeaders.COOKIE);
        HttpCookie httpCookie = new HttpCookie(cookie);

        return httpCookie.getSession();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestHeader that = (RequestHeader) o;
        return Objects.equals(headerValue, that.headerValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerValue);
    }
}
